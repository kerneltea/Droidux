package info.izumin.android.droidux.thunk;

import info.izumin.android.droidux.Action;
import rx.Observable;

/**
 * Created by izumin on 11/29/15.
 */
public interface AsyncAction extends Action {
    Observable<Action> call();
}