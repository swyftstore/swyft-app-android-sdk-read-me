package com.swyft.DemoApp;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.OnClick;
import butterknife.ButterKnife;

import com.swyftstore.sdk.firestore.model.Order;
import com.swyftstore.sdk.firestore.model.SwyftPaymentMethod;
import com.swyftstore.sdk.networking.model.SwyftUser;
import com.swyftstore.sdk.singletons.SwyftSdk;
import com.swyftstore.sdk.utils.SwyftUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private SwyftUser customer;
    private String swyftId;
    private ImageView qrImage;
    private Activity activity;
    private String customToken;
    private RelativeLayout containerProgress;
    private int start = 0;
    private int limit = 20;
    private List<SwyftPaymentMethod> paymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwyftSdk.initSDK(getApplicationContext());
        final int colorInt = getApplication().getColor(R.color.colorPrimary);
        SwyftSdk.getInstance().setQrCodeColor(colorInt);
        qrImage = findViewById(R.id.image_qr_url);
        containerProgress = findViewById(R.id.progressBarContainer);
        activity = this;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    private static SwyftUser initCustomerObject(String email, String firstName,
                                                String lastName, String phoneNumber) {
        final SwyftUser customer = new SwyftUser();
        customer.setEmailAddress(email);
        if (SwyftUtils.checkForEmpty(firstName)) {
            customer.setFirstName(firstName);
        }
        if (SwyftUtils.checkForEmpty(lastName)) {
            customer.setLastName(lastName);
        }
        if (SwyftUtils.checkForEmpty(phoneNumber)) {
            customer.setPhoneNumber(phoneNumber);
        }
        return customer;
    }

    @OnClick(R2.id.enrollUser)
    public void enrollCustomer() {
        containerProgress.setVisibility(View.VISIBLE);
        customer = initCustomerObject("fake.man@swyft.com", "Fake",
                "Man", "+14152346543");

        SwyftSdk.getInstance().enrollUser(customer, new SwyftSdk.EnrollCallBack() {
            @Override
            public void onSuccess(String customerId) {
                containerProgress.setVisibility(View.INVISIBLE);
                Log.d("Demo App", "onSuccess: " + customerId);
                swyftId = customerId;
            }

            @Override
            public void onFailure(String msg) {
                containerProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Enroll onFailure: " + msg, Toast.LENGTH_LONG).show();

                Log.e("Demo App", msg);
            }
        });
    }

    @OnClick(R2.id.authUserIdToken)
    public void authCustomer() {
        containerProgress.setVisibility(View.VISIBLE);
        SwyftSdk.getInstance().authenticateUser(swyftId, new SwyftSdk.AuthUserCallBack() {
            @Override
            public void onSuccess(Bitmap qrCode) {
                containerProgress.setVisibility(View.INVISIBLE);
                //Log.e("Demo App", "onSuccess: " + authToken);
                qrImage.setImageBitmap(qrCode);
                qrImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String msg) {
                containerProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Auth onFailure: " + msg, Toast.LENGTH_LONG).show();

                Log.e("Dempo App", "onFailure " + msg);
            }
        });
    }

    @OnClick(R2.id.authUserCustomToken)
    public void authCustomCustomer() {
        containerProgress.setVisibility(View.VISIBLE);
        customToken = "fake.man@swyft.com";
        SwyftSdk.getInstance().authenticateUser(swyftId, customToken, new SwyftSdk.AuthUserCallBack() {
            @Override
            public void onSuccess(Bitmap qrCode) {
                containerProgress.setVisibility(View.INVISIBLE);
                qrImage.setImageBitmap(qrCode);
                qrImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String msg) {
                containerProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Auth onFailure: " + msg, Toast.LENGTH_LONG).show();

                Log.e("Dempo App", "onFailure " + msg);
            }
        });
    }

    @OnClick(R2.id.getOrdersBtn)
    public void getUserOrders() {

        containerProgress.setVisibility(View.VISIBLE);
        SwyftSdk.getInstance().getOrders(start, limit, new SwyftSdk.GetOrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                for (int i = 0; i < orders.size(); i++) {
                    containerProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Orders Succesfully fetched!", Toast.LENGTH_LONG).show();
                    Log.d("Orders Quantity: ", String.valueOf(orders.size()));
                    Log.d("Orders Merchant Name: ", orders.get(i).getMerchantName());
                    Log.d("Orders Updated Time: ", String.valueOf(orders.get(i).getUpdateDateTime()));
                    Log.d("Orders Subtotal: ", String.valueOf(orders.get(i).getSubTotal()));
                }
            }

            @Override
            public void onFailure(String msg) {
                containerProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Get Orders onFailure: " + msg, Toast.LENGTH_LONG).show();

                Log.e("Demo App", "onFailure " + msg);
            }
        });
    }

    @OnClick(R2.id.getPaymentsBtn)
    public void getPaymentsMethodsUser() {
        getPayments();
    }

    private void getPayments() {
        containerProgress.setVisibility(View.VISIBLE);
        SwyftSdk.getInstance().getPaymentsMethods(new SwyftSdk.GetPaymentsCallback() {
            @Override
            public void onSuccess(List<SwyftPaymentMethod> paymentMethods) {
                containerProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Payments Succesfully fetched!", Toast.LENGTH_LONG).show();
                Log.d("Payments: ", String.valueOf(paymentMethods.size()));
                for (int i = 0; i < paymentMethods.size(); i++) {
                    Log.d("Payments Token: ", paymentMethods.get(i).getToken());
                    Log.d("Payments Card Expiry: ", paymentMethods.get(i).getCardExpiry());
                    Log.d("Payments CardholderName: ", paymentMethods.get(i).getCardholderName());
                    Log.d("Payments Card Type: ", paymentMethods.get(i).getCardType());
                    Log.d("Payments Last4: ", paymentMethods.get(i).getLast4());
                    Log.d("merchantRefs: ", paymentMethods.get(i).getMerchantRef());

                }

                /*For purpose test im taking merchantRef in 4 position of the paymentMethods to edit.
                  In the client app, provider must take this value i.e: from the position of the cards in the list by clicking them or
                  selecting them.
                */

                MainActivity.this.paymentMethods = paymentMethods;// token related: 2967532568929490
            }

            @Override
            public void onFailure(String msg) {
                containerProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Get Payments onFailure: " + msg, Toast.LENGTH_LONG).show();

                Log.e("Demo App", "onFailure " + msg);
            }
        });
    }

    @OnClick(R2.id.addPaymentBtn)
    public void addPaymentMethodUser() {
        containerProgress.setVisibility(View.VISIBLE);
        SwyftSdk.getInstance().addPaymentMethod(
                "4475091531509013",
                "VISA",
                "0520",
                "409",
                "JUAN SANDOVAL",
                false,
                new SwyftSdk.AddPaymentCallback() {
                    @Override
                    public void onSuccess(SwyftPaymentMethod paymentMethods) {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Payment Succesfully Added!", Toast.LENGTH_LONG).show();
                        Log.d("Add Payment: ", paymentMethods.getToken());
                        Log.d("Add Payment: ", paymentMethods.getCardType());
                        Log.d("Add Payment: ", paymentMethods.getLast4());
                        Log.d("Add Payment: ", paymentMethods.getCardExpiry());
                        Log.d("Add Payment: ", paymentMethods.getCardholderName());
                        getPayments();
                    }

                    @Override
                    public void onFailure(String msg) {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Payment onFailure: " + msg, Toast.LENGTH_LONG).show();
                        Log.e("Demo App", "onFailure " + msg);
                    }
                });
    }

    @OnClick(R2.id.editPaymentBtn)
    public void editPaymentMethodUser() {
        containerProgress.setVisibility(View.VISIBLE);
        final SwyftPaymentMethod paymentMethod =
                findBasesOnLast4(this.paymentMethods, "9013");
        if (paymentMethod == null) {
            Toast.makeText(getApplicationContext(), "Edit Payment onFailure: payments not loaded, loading now try again", Toast.LENGTH_LONG).show();
            getPayments();
            return;
        }
        SwyftSdk.getInstance().editPaymentMethod(
                "4475091531504321",
                "0130",
                "VISA",
                "SERGIO LOPEZ",
                "329",
                paymentMethod.getToken(),
                true,
                new SwyftSdk.EditPaymentCallback() {
                    @Override
                    public void onSuccess(SwyftPaymentMethod paymentMethod) {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Payment Succesfully Updated!", Toast.LENGTH_LONG).show();
                        Log.d("Add Payment: ", paymentMethod.getToken());
                        Log.d("Add Payment: ", paymentMethod.getCardType());
                        Log.d("Add Payment: ", paymentMethod.getLast4());
                        Log.d("Add Payment: ", paymentMethod.getCardExpiry());
                        Log.d("Add Payment: ", paymentMethod.getCardholderName());
                        getPayments();
                    }

                    @Override
                    public void onFailure(String msg) {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Payment Updated onFailure: " + msg, Toast.LENGTH_LONG).show();
                        Log.e("Demo App", "onFailure " + msg);
                    }
                }
        );
    }

    @OnClick(R2.id.setDefaultPayment)
    public void setDefaultPaymentMethodUser() {
        containerProgress.setVisibility(View.VISIBLE);
        final SwyftPaymentMethod paymentMethod = findBasesOnLast4(this.paymentMethods, "1111");
        if (paymentMethod == null) {
            Toast.makeText(getApplicationContext(), "Set Payment Default onFailure: payments not loaded, loading now try again", Toast.LENGTH_LONG).show();
            getPayments();
            return;
        }
        SwyftSdk.getInstance().setDefaultPaymentMethod(
                paymentMethod.getToken(),
                new SwyftSdk.SetDefaultMethodCallback() {
                    @Override
                    public void onSuccess() {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Set  Default Payment Succesfully Added!", Toast.LENGTH_LONG).show();
                        Log.d("Default Payment: ", paymentMethod.getToken());
                        getPayments();
                    }

                    @Override
                    public void onFailure(String msg) {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Set Payment Default onFailure: " + msg, Toast.LENGTH_LONG).show();
                        Log.e("Demo App", "onFailure " + msg);
                    }
                }
        );
    }

    @OnClick(R2.id.deletePayment)
    public void deletePaymentMethodUser() {
        containerProgress.setVisibility(View.VISIBLE);
        final SwyftPaymentMethod paymentMethod = findBasesOnLast4(this.paymentMethods, "1111");
        if (paymentMethod == null) {
            Toast.makeText(getApplicationContext(), "Delete Payment onFailure: payments not loaded, loading now try again", Toast.LENGTH_LONG).show();
            getPayments();
        }
        SwyftSdk.getInstance().deletePaymentMethod(
                paymentMethod.getToken(),
                new SwyftSdk.DeleteMethodCallback() {
                    @Override
                    public void onSuccess() {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Payment Succesfully Deleted!", Toast.LENGTH_LONG).show();
                        Log.d("Delete Payment: ", paymentMethod.getToken());
                    }

                    @Override
                    public void onFailure(String msg) {
                        containerProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Payment Delete onFailure: " + msg, Toast.LENGTH_LONG).show();
                        Log.e("Demo App", "onFailure " + msg);
                    }
                }
        );
    }

    private SwyftPaymentMethod findBasesOnLast4(List<SwyftPaymentMethod> methods, String last4) {
        if (methods != null) {
            for (SwyftPaymentMethod method : methods) {
                if (method.getLast4().equals(last4)) {
                    return method;
                }
            }
        }
        return null;
    }

}
