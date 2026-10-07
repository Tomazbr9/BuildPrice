CREATE TABLE tb_clients (
                            id UUID PRIMARY KEY,
                            user_id UUID NOT NULL,
                            name VARCHAR(150) NOT NULL,
                            email VARCHAR(255),
                            phone VARCHAR(30),

                            CONSTRAINT fk_clients_user
                                FOREIGN KEY (user_id)
                                    REFERENCES tb_users(id)
);

CREATE INDEX idx_clients_user_id
    ON tb_clients(user_id);