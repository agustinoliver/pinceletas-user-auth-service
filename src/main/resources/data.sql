-- =====================================================
-- DATOS INICIALES - PINCELETAS USER AUTH SERVICE
-- =====================================================

-- Usuario ADMIN por defecto
-- Email: tomasherrado@gamil.com
-- Password: password
INSERT INTO users (
    nombre,
    apellido,
    email,
    telefono,
    password,
    role,
    activo,
    terminos_aceptados,
    calle,
    numero,
    ciudad,
    piso,
    barrio,
    pais,
    provincia,
    codigo_postal,
    manzana,
    lote,
    firebase_uid,
    provider,
    display_name,
    created_at
) VALUES (
             'tomas',
             'herrado',
             'tomasherrado@gmail.com',
             '3512711316',
             '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
             'ADMIN',
             true,
             true,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             NULL,
             CURRENT_TIMESTAMP
         );

-- =====================================================
-- Hash calculado con BCrypt para: password
-- =====================================================