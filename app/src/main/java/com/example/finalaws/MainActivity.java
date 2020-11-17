package com.example.finalaws;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.amazonaws.amplify.generated.graphql.CreateWeakInfoMutation;
import com.amazonaws.amplify.generated.graphql.GetWeakInfoQuery;
import com.amazonaws.amplify.generated.graphql.OnCreateWeakInfoSubscription;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.CreateWeakInfoInput;

public class MainActivity extends AppCompatActivity {
    private AWSAppSyncClient mAWSAppSyncClient;

    String num = "+8201094292169";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                // If you are using complex objects (S3) then uncomment
                //.s3ObjectManager(new S3ObjectManagerImplementation(new AmazonS3Client(AWSMobileClient.getInstance())))
                .build();
        query();
        //mutation();
    }

    public void query(){
        mAWSAppSyncClient.query(GetWeakInfoQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(getWeakInfoCallback);
    }


    public void mutation(){
        CreateWeakInfoInput createTodoInput = CreateWeakInfoInput.builder()
                .contact("010-7185-8942")
                .weakNum(77)
                .build();

        mAWSAppSyncClient.mutate(CreateWeakInfoMutation.builder().input(createTodoInput).build())
                .enqueue(mutationCallback);
    }

    private GraphQLCall.Callback<CreateWeakInfoMutation.Data> mutationCallback = new GraphQLCall.Callback<CreateWeakInfoMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateWeakInfoMutation.Data> response) {
            Log.i("Results", "Added Todo");
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }
    };

    private GraphQLCall.Callback<GetWeakInfoQuery.Data> getWeakInfoCallback = new GraphQLCall.Callback<GetWeakInfoQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<GetWeakInfoQuery.Data> response) {
            Log.i("Results", response.data().getWeakInfo().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("ERROR", e.toString());
        }
    };

    private AppSyncSubscriptionCall<OnCreateWeakInfoSubscription.Data> subscriptionWatcher;

    private void subscribe() {
        OnCreateWeakInfoSubscription subscription = OnCreateWeakInfoSubscription.builder().build();
        subscriptionWatcher = mAWSAppSyncClient.subscribe(subscription);
        subscriptionWatcher.execute(subCallback);
    }

    private AppSyncSubscriptionCall.Callback<OnCreateWeakInfoSubscription.Data> subCallback = new AppSyncSubscriptionCall.Callback<OnCreateWeakInfoSubscription.Data>() {
        @Override
        public void onResponse(@Nonnull Response<OnCreateWeakInfoSubscription.Data> response) {
            Log.i("Subscription", response.data().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Subscription", e.toString());
        }

        @Override
        public void onCompleted() {
            Log.i("Subscription", "Subscription completed");
        }
    };
    }