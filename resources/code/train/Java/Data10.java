{\rtf1\ansi\ansicpg1252\cocoartf1671\cocoasubrtf600
{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural\partightenfactor0

\f0\fs24 \cf0 /*\
 * Copyright (C) 2016 Square, Inc.\
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
import io.reactivex.schedulers.TestScheduler;\
import okhttp3.mockwebserver.MockResponse;\
import okhttp3.mockwebserver.MockWebServer;\
import org.junit.Before;\
import org.junit.Rule;\
import org.junit.Test;\
import retrofit2.Response;\
import retrofit2.Retrofit;\
import retrofit2.http.GET;\
\
public final class ObservableWithSchedulerTest \{\
  @Rule public final MockWebServer server = new MockWebServer();\
  @Rule public final RecordingObserver.Rule observerRule = new RecordingObserver.Rule();\
\
  interface Service \{\
    @GET("/") Observable<String> body();\
    @GET("/") Observable<Response<String>> response();\
    @GET("/") Observable<Result<String>> result();\
  \}\
\
  private final TestScheduler scheduler = new TestScheduler();\
  private Service service;\
\
  @Before public void setUp() \{\
    Retrofit retrofit = new Retrofit.Builder()\
        .baseUrl(server.url("/"))\
        .addConverterFactory(new StringConverterFactory())\
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))\
        .build();\
    service = retrofit.create(Service.class);\
  \}\
\
  @Test public void bodyUsesScheduler() \{\
    server.enqueue(new MockResponse());\
\
    RecordingObserver<Object> observer = observerRule.create();\
    service.body().subscribe(observer);\
    observer.assertNoEvents();\
\
    scheduler.triggerActions();\
    observer.assertAnyValue().assertComplete();\
  \}\
\
  @Test public void responseUsesScheduler() \{\
    server.enqueue(new MockResponse());\
\
    RecordingObserver<Object> observer = observerRule.create();\
    service.response().subscribe(observer);\
    observer.assertNoEvents();\
\
    scheduler.triggerActions();\
    observer.assertAnyValue().assertComplete();\
  \}\
\
  @Test public void resultUsesScheduler() \{\
    server.enqueue(new MockResponse());\
\
    RecordingObserver<Object> observer = observerRule.create();\
    service.result().subscribe(observer);\
    observer.assertNoEvents();\
\
    scheduler.triggerActions();\
    observer.assertAnyValue().assertComplete();\
  \}\
\}}