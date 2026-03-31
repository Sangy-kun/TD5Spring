create type ingredient_category as enum ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
create type dish_type as enum ('STARTER', 'MAIN', 'DESSERT');
create type unit as enum ('PCS', 'KG', 'L');
create type movement_type as enum ('IN', 'OUT');

create table ingredient (
    id serial primary key,
    name varchar(255) not null,
    category ingredient_category not null,
    price numeric(10,2)
);

create table dish (
    id serial primary key,
    name varchar(255) not null,
    dish_type dish_type not null,
    selling_price numeric(10,2)
);

create table dish_ingredient (
    id serial primary key,
    id_ingredient int not null references ingredient(id),
    id_dish int not null references dish(id),
    required_quantity numeric(10,2),
    unit unit
);

create table stock_movement (
    id serial primary key,
    id_ingredient int not null references ingredient(id),
    quantity numeric(10,2) not null,
    unit unit not null,
    type movement_type not null,
    creation_datetime timestamp without time zone not null
);
