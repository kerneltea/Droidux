package info.izumin.android.droidux;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Observable;

/**
 * Created by izumin on 11/28/15.
 */
public class Dispatcher {
    public static final String TAG = Dispatcher.class.getSimpleName();

    private final List<Middleware> middlewares;
    private final List<StoreImpl> storeImpls;

    public Dispatcher(List<Middleware> middlewares, StoreImpl... storeImpls) {
        this.middlewares = middlewares;
        this.storeImpls = Arrays.asList(storeImpls);
    }

    public Observable<Action> dispatch(Action action) {
        return Observable.just(action)
                .flatMap(act -> applyMiddlewaresBeforeDispatch(action))
                .doOnNext(act -> {
                    for (StoreImpl store : storeImpls) {
                        store.dispatch(action);
                    }
                })
                .flatMap(act -> applyMiddlewaresAfterDispatch(action));
    }

    private Observable<Action> applyMiddlewaresBeforeDispatch(Action action) {
        Observable<Action> o = Observable.just(action);

        for (final Middleware<?> mw : middlewares) {
            o = o.flatMap(mw::beforeDispatch);
        }
        return o;
    }

    private Observable<Action> applyMiddlewaresAfterDispatch(Action action) {
        Observable<Action> o = Observable.just(action);
        ListIterator<Middleware> iterator = middlewares.listIterator(middlewares.size());
        while (iterator.hasPrevious()) {
            final Middleware<?> mw = iterator.previous();
            o = o.flatMap(mw::afterDispatch);
        }
        return o;
    }
}
