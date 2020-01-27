package com.ozarychta;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class SignInClient {

    private static SignInClient INSTANCE = null;
    private static GoogleSignInClient googleSignInClient = null;
    private static Context ctx = null;

    private SignInClient(Context context) {
        ctx = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("576716243653-528khc4t2dv9oe1u24j38ohqdttvpghl.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public static synchronized SignInClient getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SignInClient(context);
        }
        return INSTANCE;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public static String getTokenIdFromResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            return account.getIdToken();

        } catch (ApiException e) {
            return "";
        }

    }
}
