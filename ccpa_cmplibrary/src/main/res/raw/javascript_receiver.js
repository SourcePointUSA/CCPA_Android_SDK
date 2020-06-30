addEventListener('message', handleEvent);
 function handleEvent(event) {
     try {
         JSReceiver.log(JSON.stringify(event.data, null, 2));
         if (event.data.name === 'sp.showMessage') {
             JSReceiver.onMessageReady();
             return;
         }
         const data = eventData(event);
         JSReceiver.log(JSON.stringify(data, null, 2));
         if(data.type) {
             if(data.type === 1) JSReceiver.onSavePM(JSON.stringify(data.payload));
              else JSReceiver.onAction(data.type);
               }
     } catch (err) {
         JSReceiver.log(err.stack);
        };
 };

function eventData(event) {
       return isFromPM(event) ? dataFromPM(event) : dataFromMessage(event);
};

function isFromPM(event) {
      return !!event.data.payload;
};

function dataFromMessage(msgEvent) {
     return {
        name: msgEvent.data.name,
        type: msgEvent.data.actions.length ? msgEvent.data.actions[0].data.type : null
     };
};

function dataFromPM(pmEvent) {
     const data = {
       name: pmEvent.data.name,
       type: pmEvent.data ? pmEvent.data.payload.actionType : null,
     };
     if(data.type === 1) data.payload = userConsents(pmEvent.data.payload);
      return data;
};

function userConsents(payload){
     return {
       rejectedVendors: payload.consents.vendors.rejected,
       rejectedCategories: payload.consents.categories.rejected
     }
 };