{\rtf1\ansi\ansicpg1252\cocoartf1671\cocoasubrtf600
{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural\partightenfactor0

\f0\fs24 \cf0 /*\
 * Copyright (C) 2016 Jake Wharton\
 *\
 * Licensed under the Apache License, Version 2.0 (the "License");\
 * you may not use this file except in compliance with the License.\
 * You may obtain a copy of the License at\
 *\
 *      http://www.apache.org/licenses/LICENSE-2.0\
 *\
 * Unless required by applicable law or agreed to in writing, software\
 * distributed under the License is distributed on an "AS IS" BASIS,\
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\
 * See the License for the specific language governing permissions and\
 * limitations under the License.\
 */\
package retrofit2.adapter.rxjava2;\
\
import io.reactivex.Observable;\
import io.reactivex.Observer;\
import io.reactivex.disposables.Disposable;\
import io.reactivex.exceptions.CompositeException;\
import io.reactivex.exceptions.Exceptions;\
import io.reactivex.plugins.RxJavaPlugins;\
import retrofit2.Response;\
\
final class ResultObservable<T> extends Observable<Result<T>> \{\
  private final Observable<Response<T>> upstream;\
\
  ResultObservable(Observable<Response<T>> upstream) \{\
    this.upstream = upstream;\
  \}\
\
  @Override protected void subscribeActual(Observer<? super Result<T>> observer) \{\
    upstream.subscribe(new ResultObserver<T>(observer));\
  \}\
\
  private static class ResultObserver<R> implements Observer<Response<R>> \{\
    private final Observer<? super Result<R>> observer;\
\
    ResultObserver(Observer<? super Result<R>> observer) \{\
      this.observer = observer;\
    \}\
\
    @Override public void onSubscribe(Disposable disposable) \{\
      observer.onSubscribe(disposable);\
    \}\
\
    @Override public void onNext(Response<R> response) \{\
      observer.onNext(Result.response(response));\
    \}\
\
    @Override public void onError(Throwable throwable) \{\
      try \{\
        observer.onNext(Result.<R>error(throwable));\
      \} catch (Throwable t) \{\
        try \{\
          observer.onError(t);\
        \} catch (Throwable inner) \{\
          Exceptions.throwIfFatal(inner);\
          RxJavaPlugins.onError(new CompositeException(t, inner));\
        \}\
        return;\
      \}\
      observer.onComplete();\
    \}\
\
    @Override public void onComplete() \{\
      observer.onComplete();\
    \}\
  \}\
\}\
}