insert into users (id, pseudo, email, full_name, created) values
  (0, 'pismy', 'pierre.smeyers@gmail.com', 'Pierre Smeyers', '2019-01-01 00:00:00'),
  (1, 'VeloO', 'jeanmi@gmail.com', 'Jean Michel', '2019-01-01 00:00:00'),
  (2, '6sco', 'mathieu.melis@gmail.com', 'Mathieu Melis', '2019-01-01 00:00:00');

insert into players (name, created, creator_id, user_id) values
  ('pismy', '2019-01-01 00:00:00', 0, 0),
  ('VeloO', '2019-01-01 00:00:00', 0, 1),
  ('6sco', '2019-01-01 00:00:00', 0, 2);

