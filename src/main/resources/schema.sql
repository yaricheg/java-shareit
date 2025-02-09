TRUNCATE table users CASCADE;
TRUNCATE table items CASCADE;
TRUNCATE table bookings CASCADE;
TRUNCATE table requests CASCADE;
TRUNCATE table comments CASCADE;


create table if not exists USERS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY  PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create table if not exists ITEMS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available VARCHAR(512) NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT,
  last_booking TIMESTAMP WITHOUT TIME ZONE,
  next_booking TIMESTAMP WITHOUT TIME ZONE,
  FOREIGN KEY (owner_id) REFERENCES USERS(id) ON DELETE CASCADE,
  FOREIGN KEY (comment_id) REFERENCES COMMENTS(id) ON DELETE CASCADE
);

create table if not exists BOOKINGS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(255),
  FOREIGN KEY (item_id) REFERENCES ITEMS(id) ON DELETE CASCADE,
  FOREIGN KEY (booker_id) REFERENCES USERS(id) ON DELETE CASCADE
);

create table if not exists REQUESTS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  description VARCHAR(512),
  requestor_id BIGINT,
  requested_item_id BIGINT,
  FOREIGN KEY (requestor_id) REFERENCES USERS(id) ON DELETE CASCADE,
  FOREIGN KEY (requested_item_id) REFERENCES ITEMS(request_id) ON DELETE CASCADE
);

create table if not exists COMMENTS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  text_comment VARCHAR(512),
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE,
  FOREIGN KEY (item_id) REFERENCES ITEMS(id) ON DELETE CASCADE,
  FOREIGN KEY (author_id) REFERENCES USERS(id) ON DELETE CASCADE
);





