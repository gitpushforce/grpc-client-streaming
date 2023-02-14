package com.study.server;

import com.study.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    // Unary
    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDatabase.getBalance(accountNumber))
                .buildPartial();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    // Server Streaming
    @Override
    public void withdraw(WithDrawRequest request, StreamObserver<Money> responseObserver) {
        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount(); // 10, 20, 30,...
        int balance = AccountDatabase.getBalance(accountNumber);

        if (balance < amount) {
            Status status = Status.FAILED_PRECONDITION.withDescription("No enough money, You have only " + balance);
            responseObserver.onError(status.asRuntimeException());
        }

        // all the validations passed
        for (int i = 0; i < (amount/10); i++) {
            Money money = Money.newBuilder().setValue(10).build();
            responseObserver.onNext(money);
            AccountDatabase.deductBalance(accountNumber, 10);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }
}
