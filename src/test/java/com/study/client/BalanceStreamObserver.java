package com.study.client;

import com.study.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {

    private CountDownLatch latch;

    public BalanceStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Balance balance) {
        System.out.println("Final balance: " + balance.getAmount());
    }

    @Override
    public void onError(Throwable t) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("server is done!");
        this.latch.countDown();
    }
}
