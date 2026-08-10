package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;

/** Per-user balance. Add new currencies here without changing progress records. */
@Entity @Table(name = "reward_wallets") @Data
public class RewardWallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", unique = true) private User user;
    @Column(nullable = false) private int coins = 0;
    @Column(nullable = false) private int gems = 0;
    @Column(nullable = false) private int premiumCoins = 0;
}
