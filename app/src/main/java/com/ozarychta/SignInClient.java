package com.ozarychta;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SignInClient {

    private static SignInClient INSTANCE = null;
    private static GoogleSignInClient googleSignInClient = null;

    private SignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
}
