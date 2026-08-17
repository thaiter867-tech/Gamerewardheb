/**
 * GameReward Hub - Serverless Postback API Endpoint (Vercel / Node.js)
 * Listens for Server-to-Server (S2S) HTTP GET/POST callbacks from Offerwalls (e.g. Monlix).
 * 
 * Example Offerwall Callback URL:
 * https://your-domain.vercel.app/api/postback?subId={user_id}&coins={coins}&transId={transaction_id}&secret=YOUR_SECRET_KEY
 */

const admin = require('firebase-admin');

// Initialize Firebase Admin SDK if not already initialized
if (!admin.apps.length) {
  try {
    admin.initializeApp({
      credential: admin.credential.cert({
        projectId: process.env.FIREBASE_PROJECT_ID,
        clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
        privateKey: process.env.FIREBASE_PRIVATE_KEY?.replace(/\\n/g, '\n'),
      }),
    });
  } catch (error) {
    console.error('Firebase Admin Initialization Error:', error);
  }
}

const SECRET_KEY = process.env.SECRET_KEY || 'CYBERPUNK_GAMEREWARD_SECRET_2026';

export default async function handler(req, res) {
  // Support both GET and POST requests from offerwalls
  const params = req.method === 'POST' ? req.body : req.query;
  const { subId, coins, transId, secret, offerName } = params;

  // 1. Security Check: Validate Secret Key
  if (!secret || secret !== SECRET_KEY) {
    return res.status(401).json({
      status: 'error',
      message: 'Unauthorized: Invalid secret key',
    });
  }

  // 2. Validate required parameters
  if (!subId || !coins) {
    return res.status(400).json({
      status: 'error',
      message: 'Missing required parameters: subId and coins are mandatory',
    });
  }

  const parsedCoins = parseInt(coins, 10);
  if (isNaN(parsedCoins) || parsedCoins <= 0) {
    return res.status(400).json({
      status: 'error',
      message: 'Invalid coins parameter',
    });
  }

  try {
    const db = admin.firestore();
    const userRef = db.collection('users').doc(subId);

    // 3. Deduplication check using Transaction ID (if provided by offerwall)
    if (transId) {
      const txRef = db.collection('transactions').doc(`tx_${transId}`);
      const txDoc = await txRef.get();
      if (txDoc.exists) {
        return res.status(200).json({
          status: 'success',
          message: 'Postback already processed (duplicate transId ignored)',
          transId,
        });
      }

      // Record transaction
      await txRef.set({
        transId,
        uid: subId,
        type: 'OFFERWALL_REWARD',
        coins: parsedCoins,
        title: offerName || 'Offerwall Reward',
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    } else {
      // Record transaction without custom doc ID
      await db.collection('transactions').add({
        uid: subId,
        type: 'OFFERWALL_REWARD',
        coins: parsedCoins,
        title: offerName || 'Offerwall Task Completed',
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }

    // 4. Atomically increment user's coins in Firestore
    await userRef.update({
      coins: admin.firestore.FieldValue.increment(parsedCoins),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    console.log(`[POSTBACK SUCCESS] Credited ${parsedCoins} coins to user UID: ${subId}`);

    return res.status(200).json({
      status: 'success',
      message: 'Postback processed successfully',
      subId,
      coinsCredited: parsedCoins,
    });
  } catch (error) {
    console.error('[POSTBACK ERROR]:', error);
    return res.status(500).json({
      status: 'error',
      message: 'Internal server error processing postback',
      error: error.message,
    });
  }
}
