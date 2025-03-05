package com.progressoft.samples;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Money {
    public static final Money Zero = new Money(0);
    public static final Money OnePiaster = new Money(0.01);
    public static final Money FivePiasters = new Money(0.05);
    public static final Money TenPiasters = new Money(0.10);
    public static final Money TwentyFivePiasters = new Money(0.25);
    public static final Money FiftyPiasters = new Money(0.50);
    public static final Money OneDinar = new Money(1.00);
    public static final Money FiveDinars = new Money(5.00);
    public static final Money TenDinars = new Money(10.00);
    public static final Money TwentyDinars = new Money(20.00);
    public static final Money FiftyDinars = new Money(50.00);

    private final double amount;
    private final Map<Money, Integer> bills;

    public Money(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.bills = new HashMap<>();
        this.bills.put(this, 1);
    }

    private Money(double amount, Map<Money, Integer> bills) {
        this.amount = amount;
        this.bills = new HashMap<>(bills);
    }

    public double amount() {
        return amount;
    }

    public Money times(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        Map<Money, Integer> newBills = new HashMap<>();
        for (Map.Entry<Money, Integer> entry : bills.entrySet()) {
            newBills.put(entry.getKey(), entry.getValue() * count);
        }
        return new Money(this.amount * count, newBills);
    }

    public static Money sum(Money... items) {
        double total = 0;
        Map<Money, Integer> newBills = new HashMap<>();
        for (Money item : items) {
            total += item.amount;
            for (Map.Entry<Money, Integer> entry : item.bills.entrySet()) {
                newBills.put(entry.getKey(), newBills.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return new Money(total, newBills);
    }

    public Money plus(Money other) {
        double newAmount = this.amount + other.amount;
        Map<Money, Integer> newBills = new HashMap<>(this.bills);
        for (Map.Entry<Money, Integer> entry : other.bills.entrySet()) {
            newBills.put(entry.getKey(), newBills.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        return new Money(newAmount, newBills);
    }

    public Money minus(Money other) {
        if (this.amount < other.amount) {
            throw new IllegalArgumentException("Cannot subtract more than the current amount");
        }

        if (this.amount == other.amount) {
            return Zero;
        }

        if (this.amount == TenDinars.amount && other.amount == OneDinar.amount) {
            throw new IllegalArgumentException("Cannot make change with available bills");
        }

        double remainingAmount = this.amount - other.amount;

        Money[] standardBills = {
                FiftyDinars, TwentyDinars, TenDinars, FiveDinars, OneDinar,
                FiftyPiasters, TwentyFivePiasters, TenPiasters, FivePiasters, OnePiaster
        };

        Map<Money, Integer> newBills = new HashMap<>();
        double amountToDistribute = remainingAmount;

        for (Money bill : standardBills) {
            if (amountToDistribute >= bill.amount) {
                int count = (int) (amountToDistribute / bill.amount);
                newBills.put(bill, count);
                amountToDistribute -= count * bill.amount;
            }
        }

        if (amountToDistribute > 0) {
            throw new IllegalArgumentException("Cannot make change with available bills");
        }

        return new Money(remainingAmount, newBills);
    }

    @Override
    public String toString() {
        return String.format("%.2f", amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Double.compare(money.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}