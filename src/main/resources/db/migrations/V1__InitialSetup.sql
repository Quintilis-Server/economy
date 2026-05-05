-- Listing
CREATE TABLE listings(
     id                      SERIAL PRIMARY KEY ,
     seller_uuid             UUID                                NOT NULL
         REFERENCES players(id),
     quantity                INTEGER                             NOT NULL,
     asking_price_per_item   INTEGER                             NOT NULL,
     created_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL ,
     expires_at              TIMESTAMP WITH TIME ZONE DEFAULT (CURRENT_TIMESTAMP + '2 HOURS') NOT NULL ,
     status                  VARCHAR(30) DEFAULT 'ACTIVE'        NOT NULL,
     item_data               BYTEA                               NOT NULL
);

CREATE INDEX idx_listing_seller_status
    ON listings(seller_uuid, status);

CREATE TABLE market_transaction_details(
    transaction_id      INTEGER NOT NULL PRIMARY KEY
        REFERENCES transactions,
    listing_id          INTEGER NOT NULL
        REFERENCES listings,
    quantity            INTEGER NOT NULL
);
