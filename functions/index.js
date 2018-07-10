const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.messageNotify = functions.database.ref('/Messages/{chatID}/{pushID}/userId')
    .onCreate((snapshot, context) => {
        // Grab the current value of what was written to the Realtime Database.
        const senderId = snapshot.val();
        console.log('Sender: ', senderId);
        const chatId = context.params.chatID;
        var receiverId;

        //getting receiverId from the Chats
        var chatRef = admin.database().ref("Chats");
        chatRef.orderByChild("id").equalTo(chatId).on("child_added", snapshot => {
            const user1ID = snapshot.child('user1id').val();
            const user2ID = snapshot.child('user2id').val();
            if (senderId === user1ID) receiverId = user2ID;
            else receiverId = user1ID;
            console.log(receiverId);
        });

        //const getInstanceIdPromise = admin.database().ref(`/Users/${receiverId}/uid`).once('value');
        //const getSenderUidPromise = admin.auth().getUser(senderId);

        //return Promise.all([getInstanceIdPromise, getSenderUidPromise]).then(results => {
        //    const instanceId = results[0].val();
        //    const sender = results[1];
        //    console.log('notifying ' + receiverUid + ' from ' + senderUid);

        //    const payload = {
        //        notification: {
        //            title: sender.displayName,
        //            body: snapshot.ref.parent.child('text').val(),
        //            icon: sender.photoURL
        //        }
        //    };

        //    admin.messaging().sendToDevice(instanceId, payload)
        //        .then(function (response) {
        //            console.log("Successfully sent message:", response);
        //        })
        //        .catch(function (error) {
        //            console.log("Error sending message:", error);
        //        });
        //});

        //return snapshot.ref.parent.child('text').set("Message");
    });


const TIME_ZONE_CORRECTION = 2 * 60 * 60 * 1000; // 2 Hours in milliseconds.

exports.deleteOldRides = functions.database.ref('/Rides/{pushId}').onWrite((change) => {
    const ref = change.after.ref.parent; // reference to the parent
    const now = new Date();
    const nowCorrected = new Date(now.getTime() + TIME_ZONE_CORRECTION);

    const oldItemsQuery = ref.orderByChild('date');

    return oldItemsQuery.once('value').then((snapshot) => {
        const updates = {};
        snapshot.forEach(ride => {
            var dateSplit = ride.child('date').val().split("/");
            var timeSplit = ride.child('time').val().split(":");
            var dateTime = new Date(parseInt("20" + dateSplit[2]), parseInt(dateSplit[1]) - 1, parseInt(dateSplit[0]), parseInt(timeSplit[0]), parseInt(timeSplit[1]));
            if (dateTime < nowCorrected) {
                console.log('This one will be deleted' + dateTime);
                updates[ride.key] = null;
            }
        });
        return ref.update(updates);
    });
});