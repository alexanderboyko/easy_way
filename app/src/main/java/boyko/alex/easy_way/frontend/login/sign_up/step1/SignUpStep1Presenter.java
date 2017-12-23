package boyko.alex.easy_way.frontend.login.sign_up.step1;

import android.support.annotation.NonNull;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 03.11.2017.
 */

class SignUpStep1Presenter {
    private SignUpStep1ViewActivity view;
    private static SignUpStep1Presenter presenter;

    private SignUpStep1Presenter(SignUpStep1ViewActivity view) {
        this.view = view;
    }

    static SignUpStep1Presenter getInstance(SignUpStep1ViewActivity view) {
        if (presenter == null) {
            presenter = new SignUpStep1Presenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onFabClicked(@NonNull String email,@NonNull String password,@NonNull String passwordRepeat){
        if(isValid(email,password,passwordRepeat)) {
            view.launchStep2Activity();
        }
    }

    private boolean isValid(@NonNull String email, @NonNull String password,@NonNull String passwordRepeat){
        boolean isValid = true;

        if(email.isEmpty()){
            view.showEmailError(ApplicationController.getInstance().getString(R.string.error_empty));
            isValid = false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            view.showEmailError(ApplicationController.getInstance().getString(R.string.error_email));
            isValid = false;
        }

        if(password.isEmpty()){
            view.showPasswordError(ApplicationController.getInstance().getString(R.string.error_empty));
            isValid = false;
        }else if(password.length() < 8){
            view.showPasswordError(ApplicationController.getInstance().getString(R.string.error_short_password));
            isValid = false;
        }

        if(passwordRepeat.isEmpty()){
            view.showPasswordRepeatError(ApplicationController.getInstance().getString(R.string.error_empty));
            isValid = false;
        } else if(!password.isEmpty() && !passwordRepeat.isEmpty() && !password.equals(passwordRepeat)){
            view.showPasswordRepeatError(ApplicationController.getInstance().getString(R.string.error_passwords_not_equals));
            isValid = false;
        }

        return isValid;
    }
}
