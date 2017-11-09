package boyko.alex.easy_way.frontend.login.sign_up.step1;

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

    void onFabClicked(){
        //todo validation
        view.launchStep2Activity();
    }
}
