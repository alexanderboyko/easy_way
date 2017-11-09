package boyko.alex.easy_way.frontend.login.sign_in;

/**
 * Created by Sasha on 03.11.2017.
 */

class SignInPresenter {
    private SignInViewActivity view;
    private static SignInPresenter presenter;

    private SignInPresenter(SignInViewActivity view) {
        this.view = view;
    }

    static SignInPresenter getInstance(SignInViewActivity view) {
        if (presenter == null) {
            presenter = new SignInPresenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onForgotPasswordClicked(){

    }
}
