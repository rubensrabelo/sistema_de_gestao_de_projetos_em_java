CREATE TABLE tb_collaborators (
                                  id SERIAL PRIMARY KEY,
                                  name VARCHAR(100) NOT NULL,
                                  email VARCHAR(150) UNIQUE NOT NULL,
                                  function VARCHAR(50) NOT NULL
);