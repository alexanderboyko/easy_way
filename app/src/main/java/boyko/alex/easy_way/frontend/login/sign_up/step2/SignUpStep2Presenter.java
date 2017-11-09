package boyko.alex.easy_way.frontend.login.sign_up.step2;

/**
 * Created by Sasha on 03.11.2017.
 */

class SignUpStep2Presenter {
    private SignUpStep2ViewActivity view;
    private static SignUpStep2Presenter presenter;

    private SignUpStep2Presenter(SignUpStep2ViewActivity view) {
        this.view = view;
    }

    static SignUpStep2Presenter getInstance(SignUpStep2ViewActivity view) {
        if (presenter == null) {
            presenter = new SignUpStep2Presenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onBirthdayChange(){

    }

    void onSexChange(){

    }

    void onSignUpClicked(){

    }
}
