insert into users (id, pseudo, email, password, full_name, created) values
  ('aaaaaaaa', 'pismy', 'pierre.smeyers@gmail.com', '$2a$10$ZuhGEMWK0aWgibbBNmv.IuyeQ4Yeu/HhLmEOYqny3G9V3jV7/61Oi', 'Pierre Smeyers', '2019-01-01 00:00:00'),
  ('bbbbbbbb', 'VeloO', 'jeanmi@gmail.com', '$2a$10$ZuhGEMWK0aWgibbBNmv.IuyeQ4Yeu/HhLmEOYqny3G9V3jV7/61Oi', 'Jean Michel', '2019-01-01 00:00:00'),
  ('cccccccc', '6sco', 'mathieu.melis@gmail.com', '$2a$10$ZuhGEMWK0aWgibbBNmv.IuyeQ4Yeu/HhLmEOYqny3G9V3jV7/61Oi', 'Mathieu Melis', '2019-01-01 00:00:00');

insert into players (name, created, creator_id, user_id) values
  ('pismy', '2019-01-01 00:00:00', 'aaaaaaaa', 'aaaaaaaa'),
  ('VeloO', '2019-01-01 00:00:00', 'aaaaaaaa', 'bbbbbbbb'),
  ('6sco', '2019-01-01 00:00:00', 'aaaaaaaa', 'cccccccc');

