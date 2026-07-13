CREATE TABLE roles
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(50) NOT NULL UNIQUE
);



CREATE TABLE users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE,

    email VARCHAR(100) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    balance DECIMAL(10,2) NOT NULL DEFAULT 1000.00,

    role_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id)
            REFERENCES roles(id)
);



CREATE TABLE games
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL,

    player_score INT NOT NULL DEFAULT 0,

    dealer_score INT NOT NULL DEFAULT 0,

    bet_amount DECIMAL(10,2) NOT NULL,

    result VARCHAR(30),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_games_users
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);



CREATE TABLE game_rounds
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    game_id BIGINT NOT NULL,

    round_number INT NOT NULL,

    player_action VARCHAR(30),

    player_score INT NOT NULL,

    dealer_score INT NOT NULL,


    CONSTRAINT fk_rounds_games
        FOREIGN KEY (game_id)
            REFERENCES games(id)
);



CREATE TABLE cards
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    game_round_id BIGINT NOT NULL,

    owner VARCHAR(20) NOT NULL,

    suit VARCHAR(20) NOT NULL,

    card_rank VARCHAR(20) NOT NULL,

    value INT NOT NULL,


    CONSTRAINT fk_cards_rounds
        FOREIGN KEY (game_round_id)
            REFERENCES game_rounds(id)
);



CREATE TABLE transactions
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    amount DECIMAL(10,2) NOT NULL,

    type VARCHAR(30) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_transactions_users
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);