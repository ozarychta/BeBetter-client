package com.ozarychta.bebetter.utils;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInClient {

    private static final String API_KEY = "PUT_YOUR_API_KEY_HERE";
    private static SignInClient INSTANCE = null;
    private static GoogleSignInClient googleSignInClient = null;
    private static Context ctx = null;

    private SignInClient(Context context) {
        ctx = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(API_KEY)
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

    public Task<GoogleSignInAccount> silentSignIn() {
        return googleSignInClient.silentSignIn();
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
