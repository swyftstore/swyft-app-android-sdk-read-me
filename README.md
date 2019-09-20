# swyft-app-sdk-android

This project is to be used by a 3rd party android application, inorder to integrate with a Swyft Vission Cabinet


### Table of Contents 
- [Setup](#setup)
  - [Request SDK Credentials](#creds)
  - [Installation](#install)
- [Usage](#usage)
  - [SDK Initilization](#init)
  - [User Enrollment](#enroll)
  - [User Authentication](#auth)
  - [Get User Orders](#orders)
  - [Get User Payment Methods](#pMethods)
  - [Add User Payment Method](#addPMethod)
  - [Update User Payment Method](#updatePMethod)
  - [Set User Default Payment Method](#setDefaultPMethod)
  - [Delete User Payment Method](#removePMethod)
- [Webhooks](#webhooks)
  - [Session Begins](#webhookBegins)
  - [Session Ends](#webhookEnd)

<a name="setup"/>

## Setup 

Before using the Swyft SDK you will need request access credentials and install the library into your project

<a name="creds"/>

### Request SDK credentials 

In order to use the SDK you need to request credentials by supplying swyft with your Applications Bundle ID. The credentials will come in the form of a json file that you will need to include in your applications build path. 

Sample JSON file swyft-sdk.json
```javascript
{
    "key": "lkfjasfhiqf9ufoisadfhkashfuuf092u9fh2iub2hbfuyg23uygd2h98f2hfb",
    "world_net: {
      "secret": "i98809nfas",
      "terminalId": "00000001"
    }
}
```
<a name="install"/>

### Installation

To include the Swyft SDK in your project, add the following to your app level build.gradle.

```javascript
dependencies {
   ...
   implementation  'com.swyft.sdk:1.0.0'
}
```

Note: we haven't decided if we want to publish our sdk to maven or distribute through more closed channels 

<a name="usage"/>

## Usage

Below you will find some code examples for how you can intergate the sdk with your application

<a name="init"/>

### SDK Initilization

The first step when integrating the Swyft SDK with your project is to call the initializion methond on the SDK. We recommend doing this on your main activity's onCreate method. The initializion method takes in the application context. This is used to access some local resources within the SDK. 
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SwyftSdk.initSDK(getApplicationContext());
}
```

<a name="enroll"/>

### Enroll User 

The next is to enroll your applications user with Swyft. This is used to create a profile for your user on Swyft's Platform. The method returns a swyftId that is used to authenticate the user later on. If the user already exist the SDK blocks the creation of a duplicate user and returns the swyftId successfully. 
```java
final SwyftUser user = new SwyftUser();
       user.setEmailAddress(email);
       user.setFirstName(firstName);
       user.setLastName(lastName);
       user.setPhoneNumber(phoneNumber);

SwyftSdk.getInstance().enrollUser(user, new SwyftSdk.EnrollCallback() {
    @Override
    public void onSuccess(String swyftId) {
        Log.d("Demo App", "onSuccess: " + swyftId);
        //Store the swyftId for later usage.
        this.swyftId = swyftId;
    }

    @Override
    public void onFailure(String msg) {
        Log.e("Demo App", msg);
    }
});
    
```    
<a name="auth"/>

### User Authentication 

After you have enrolled a user you can authenticate the user. This creates a session for the user so they can interact with the Swyft Vision Cabinet by scanning a returned QR Code. You can either supply an authentication string for the SDK to display as a QR Code or have the SDK generate a dynamic one for you. 

- Auto Generated QR Code
```java
 SwyftSdk.getInstance().authenticateUser(swyftId, new SwyftSdk.AuthUserCallback() {
    @Override
    public void onSuccess(Bitmap qrCode) {
        qrImage.setImageBitmap(qrCode);
    }

    @Override
    public void onFailure(String msg) {
        Log.e("Dempo App", "onFailure " + msg);
    }
}); 
``` 
- Custom Generated QR Code
```java
final String customToken = "my custom authentication string";
SwyftSdk.getInstance().authenticateUser(swyftId, customToken, new SwyftSdk.AuthUserCallback() {
    @Override
    public void onSuccess(Bitmap qrCode) {
        qrImage.setImageBitmap(qrCode);
    }

    @Override
    public void onFailure(String msg) {
        Log.e("Dempo App", "onFailure " + msg);
    }
});
```
- Setting QR Code Color
You can set the foreground color of the displayed QR Code by setting the QR Code color on the Swyft SDK.
```java 
 final int colorInt = getApplication().getColor(R.color.colorPrimary);
 SwyftSdk.getInstance().setQrCodeColor(colorInt);
```
<a name="orders"/>

### Get User Orders

After athenticating a user you can retrieve their past orders. Orders will be returned in a paginated list. 
```java
SwyftSdk.getInstance().getOrdersUser(start, limit, new SwyftSdk.GetOrdersCallback() {
        @Override
        public void onSuccess(List<Order> orders) {
           //store/display orders to users here
        }

        @Override
        public void onFailure(String msg) {
            Log.e("Demo App", "onFailure " + msg);
        }
    });
}

```
<a name="pMethods"/>

### Get User Payment Methods

If Swyft is handling the payment processing for your integration, after you authenticate the user you can retreive a list of their previously enrolled payment methods
```java
SwyftSdk.getInstance().getPaymentsMethods(new SwyftSdk.GetPaymentsCallback() {
    @Override
    public void onSuccess(List<SwyftPaymentMethod> paymentMethods) {
       //store/display payment methods to users here
    }

    @Override
    public void onFailure(String msg) {
        Log.e("Demo App", "onFailure " + msg);
    }
});
```
<a name="addPMethod"/>

### Add User Payment Methods

If Swyft is handling the payment processing for your integration, after you authenticate the user you can add additional payment methods for the user
```java
SwyftSdk.getInstance().addPaymentMethod(
        "4475091531509013",
        "VISA",
        "0520",
        "409",
        "John Smtih",
        false,//is used to set the payment method as the 'default' method. If this is the first/only method for   
              //the user it is ALWAYS treated as true
    new SwyftSdk.AddPaymentCallback() {
    @Override
    public void onSuccess(SwyftPaymentMethod paymentMethod) {
       //add to the list of payment methods for the user
    }

    @Override
    public void onFailure(String msg) {
        Log.e("Demo App", "onFailure " + msg);
    }
});
```
<a name="updatePMethod"/>

### Update User Payment Method

If Swyft is handling the payment processing for your integration, after you authenticate the user you can update payment methods for the user
```java
//load previously stored payment method you wish to update
final SwyftPaymentMethod paymentMethod = this.paymentMethods.get(0); 
SwyftSdk.getInstance().editPaymentMethod(
        "4475091531509013",
        "0127",
        "VISA",
        "Johnathan Smith",
        "409",
        paymentMethod.getToken(),//the payment method's token is a unique identifier 
    new SwyftSdk.EditPaymentCallback() {
        @Override
        public void onSuccess(SwyftPaymentMethod paymentMethod) {
            //load updated paymentMethod here
        }

        @Override
        public void onFailure(String msg) {
            Log.e("Demo App", "onFailure " + msg);
        }
});
```
<a name="setDefaultPMethod"/>

### Set User Default Payment Method

If Swyft is handling the payment processing for your integration, after you authenticate the user you can set the default payment method
```java
//load previously stored payment method you wish to set as the default method
final SwyftPaymentMethod paymentMethod = this.paymentMethods.get(0);
SwyftSdk.getInstance().setDefaultPaymentMethod(
        paymentMethod.getToken(),
        new SwyftSdk.SetDefaultMethodCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String msg) {
               Log.e("Demo App", "onFailure " + msg);
            }
        }
);
```
<a name="removePMethod"/>

### Delete User Payment Method

If Swyft is handling the payment processing for your integration, after you authenticate the user you can delete a payment method
```java
//load previously stored payment method you wish to set as the default method
final SwyftPaymentMethod paymentMethod = this.paymentMethods.get(0);
SwyftSdk.getInstance().deletePaymentMethod(
        paymentMethod.getToken(),
        new SwyftSdk.DeleteMethodCallback() {
            @Override
            public void onSuccess() {
                //update payment method list
            }

            @Override
            public void onFailure(String msg) {
                Log.e("Demo App", "onFailure " + msg);
            }
        }
);

```
<a name="webhooks"/>

## Webhooks

Swyft offers a pair of webhooks that you can choose to integrate with along with the SDK. The webhooks can alert you eachtime one of your users begin a session with a Swyft Vision Cabinet, as well as once the session ends and its transaction details. If you wish to intgrate with the Swyft Vision Cabinet webhooks below are the payloads you can expect once you supply Swyft with your end points. 

<a name="webhookBegins"/>

### Session Begins

Session Begins WebHook Payload
```javascript
payload: {
  email_address:String,
  preauth_amount: String,
  storeId: String,
  customerId: String
}

```

<a name="webhookEnd"/>

### Session Ends

Session Ends WebHook Payload
```javascript
payload: {
  email_address:String,
  products: Array,
  store_id: String,
  customer_id: String,
  subtotal: String,
  tax: String,
  total: String
}
```
