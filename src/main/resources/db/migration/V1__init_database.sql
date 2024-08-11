-- users table --
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role SMALLINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`)
);

-- projects table --
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_projects_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_projects_user_id ON projects(user_id);

-- models table --
CREATE TABLE IF NOT EXISTS models (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    project_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_models_project_id FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);
CREATE INDEX idx_models_project_id ON models(project_id);

-- experiments table --
CREATE TABLE IF NOT EXISTS experiments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    model_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiments_model_id FOREIGN KEY (model_id) REFERENCES models(id) ON DELETE CASCADE
);
CREATE INDEX idx_experiments_model_id ON experiments(model_id);

-- experiment_results table --
CREATE TABLE IF NOT EXISTS experiment_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    final_step INTEGER NOT NULL,
    experiment_id BIGINT NOT NULL,
    url VARCHAR(300),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiment_results_experiment_id FOREIGN KEY (experiment_id) REFERENCES experiments(id) ON DELETE CASCADE
);
CREATE INDEX idx_experiment_results_experiment_id ON experiment_results(experiment_id);

CREATE TABLE IF NOT EXISTS experiment_result_images (
    id BIGINT NOT NULL AUTO_INCREMENT,
    experiment_result_id BIGINT NOT NULL,
    image_url VARCHAR(300),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiment_result_images_experiment_result_id FOREIGN KEY (experiment_result_id) REFERENCES experiment_results(id) ON DELETE CASCADE
);
CREATE INDEX idx_experiment_result_images_experiment_result_id ON experiment_result_images(experiment_result_id);

-- nodes table --
CREATE TABLE IF NOT EXISTS nodes (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    role TINYINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    PRIMARY KEY (`id`)
)
