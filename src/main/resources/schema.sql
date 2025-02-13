--TRUNCATE table users CASCADE;
--TRUNCATE table items CASCADE;
--TRUNCATE table bookings CASCADE;
--TRUNCATE table requests CASCADE;
--TRUNCATE table comments CASCADE;


CREATE TABLE IF NOT EXISTS USERS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email),
  CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ITEMS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available VARCHAR(512) NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT,
  last_booking TIMESTAMP WITHOUT TIME ZONE,
  next_booking TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_items PRIMARY KEY (id),
  CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES USERS(id) ON DELETE CASCADE,
  CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES REQUESTS(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(255),
  CONSTRAINT pk_bookings PRIMARY KEY (id),
  CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES ITEMS (id) ON DELETE CASCADE,
  CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES USERS (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUESTS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(512),
  requestor_id BIGINT,
  CONSTRAINT pk_item_requests PRIMARY KEY (id),
  CONSTRAINT fk_requestor FOREIGN KEY (requestor_id) REFERENCES USERS(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text_comment VARCHAR(512),
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_comments PRIMARY KEY (id),
  CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES ITEMS(id) ON DELETE CASCADE,
  CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES USERS(id) ON DELETE CASCADE
);





