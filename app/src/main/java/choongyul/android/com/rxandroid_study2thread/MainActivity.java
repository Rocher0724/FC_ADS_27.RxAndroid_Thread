package choongyul.android.com.rxandroid_study2thread;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView text1, text2, text3;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(v -> {
            // 버튼클릭시 io thread 실행하게 구현을 해볼까?
        });

        // 실제 Task 처리하는 객체 (발행자)
        Observable<String> simpleObservable =
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        for(int i=0 ; i<10 ; i++) {
                            subscriber.onNext("Hello RxAndroid " + i);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        subscriber.onCompleted();
                    }
                });

        // 기본형 - thread를 지정해준다..
        simpleObservable
                .subscribeOn(Schedulers.io())// 이 함수가 어떤스레드에서 돌아갈지 정해주는 메소드
                                             // 발행자를 별도의 thread에서 동작시킨다
                .observeOn(AndroidSchedulers.mainThread()) // 이 옵저버에 해당되는 애들만 메인스레드에서 돈다.
                .subscribe(new Subscriber<String>() { // observer(구독자) 에 해당함
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "[observer1] complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "[observer1] error : " + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        text1.setText("observer1 " + s);
                    }
                });

                // 옵저버 (구독자) 를 등록해주는 함수 - 기본형


        // 옵저버를 등록하는 함수 - 진화형 (각 함수를 하나의 콜백객체에 나눠서 담아준다)
        simpleObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                new Action1<String>() { // onNext함수와 동일한 역할을 하는 콜백 객체
                    @Override
                    public void call(String s) {
                        text2.setText("observer2 " + s);

                    }
                }, new Action1<Throwable>() { // onError 함수와 동일한 역할을 하는 콜백 객체
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "[observer2] error : " + throwable.getMessage());

                    }
                }, new Action0() { // onComplete 함수와 동일한 역할을 하는 콜백 객체
                    @Override
                    public void call() {
                        Log.d(TAG, "[observer2] complete!");


                    }
                }
        );

        // 옵저버를 등록하는 함수 - 최종진화형 람다형
        simpleObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( // 순서가 정해져 있기 때문에 처음 s는 onNext의 string으로,
                                    // 두번째 인자는 onError
                                    // 세번째 인자는 onComplete로 인식한다. 까보면됌
                s -> {text3.setText("observer3 " + s);}
                , throwable -> {Log.e(TAG, "[observer3] error : " + throwable.getMessage());}
                , () -> {Log.d(TAG, "[observer3] complete!");}

        );
    }

}







