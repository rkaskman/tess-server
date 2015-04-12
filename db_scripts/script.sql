CREATE USER faust WITH PASSWORD 'faustf';
CREATE DATABASE financemanager;
GRANT ALL PRIVILEGES ON DATABASE financemanager TO faust;


create SEQUENCE receipt_picture_data_seq;

create TABLE receipt_picture_data
(
  id NUMERIC(10, 0) not null DEFAULT nextval('receipt_picture_data_seq'),
  user_id text NOT NULL ,
  registration_id text NOT NULL ,
  regNumber_picture text NOT NULL,
  reg_number_picture_extension text  NOT NULL,
  total_cost_picture text NOT NULL,
  total_cost_picture_extension text NOT NULL ,
  state VARCHAR(1) NOT NULL ,
  inserted_at timestamp NOT NULL
);

create SEQUENCE processed_image_result_seq;

create TABLE processed_image_result
(
  id NUMERIC(10, 0) not null DEFAULT nextval('processed_image_result_seq'),
  user_id text NOT NULL ,
  registration_id text NOT NULL ,
  company_reg_number text NOT NULL,
  company_name text  NOT NULL,
  total_cost NUMERIC(10, 2) NOT NULL ,
  state VARCHAR(1) NOT NULL ,
  inserted_at timestamp NOT NULL
);
