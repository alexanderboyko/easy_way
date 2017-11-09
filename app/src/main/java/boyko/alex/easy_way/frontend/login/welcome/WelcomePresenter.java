package boyko.alex.easy_way.frontend.login.welcome;

/**
 * Created by Sasha on 03.11.2017.
 */

class WelcomePresenter {
    private WelcomeViewActivity view;
    private static WelcomePresenter presenter;

    private WelcomePresenter(WelcomeViewActivity view){
        this.view = view;
    }

    static WelcomePresenter getInstance(WelcomeViewActivity view){
        if(presenter == null){
            presenter = new WelcomePresenter(view);
        }else{
            presenter.view = view;
        }
        return presenter;
    }

    void onTermsClicked(){
        view.launchTermsActivity();
    }

    void onFacebookLoginClicked(){

    }
}
