INSERT INTO training_types (training_type_name)
SELECT 'Cardio' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Cardio');
INSERT INTO training_types (training_type_name)
SELECT 'Strength' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Strength');
INSERT INTO training_types (training_type_name)
SELECT 'Yoga' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Yoga');
