INSERT INTO vehicle_type (vehicle_type)
VALUES ('PERSONAL')
ON CONFLICT (vehicle_type) DO NOTHING;

INSERT INTO vehicle_type (vehicle_type)
VALUES ('COMMERCIAL')
ON CONFLICT (vehicle_type) DO NOTHING;