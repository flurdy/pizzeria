# Pizzeria schema

# --- !Ups

CREATE TABLE pizza (
  id serial NOT NULL,
  name varchar(128) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE pizza_order (
  id serial NOT NULL,
  pizza_id int NOT NULL REFERENCES pizza (id),
  order_date TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (id)
);


# --- !Downs

DROP TABLE pizza_order;
DROP TABLE pizza;
