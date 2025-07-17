# VentasElExito-Comida

Este proyecto es un sistema de gestión para un restaurante de comida rápida llamado **"El Éxito"**, que permite administrar productos, empleados, clientes, pedidos, pagos etc. Este incluye una base de datos relacional en MySQL con procedimientos almacenados, consultas y triggers para hacer mas faciles algunas acciones del negocio.

## Contenido del Repositorio

- Un proyecto en NetBeans usando JavaFx y con Maven
- Una base de datos para manejar mejor la gestion de los registros, que incluye:
- Creación de tablas 
- Insertaciones de datos de prueba 
- Procedimientos almacenados 
- Triggers 
- Consultas personalizadas

## Como adicional forma de navegar

- Al estar en la Pantalla de Inicio debera registrace o ya se iniciar sesion con una de estas dos cuentas
- Usuario : Admin / Password : 1234  - Este usuario lo enlazara con la vista contrlador de las entidades - Entorno Administrador
- Usuario : User / Password : 1234 - Este usuario ingresara como un cliente nomal y tendra las opciones de realizar una compra y visualizar su historial de Compras - Entorno usuario

### Script SQL completo de la base de datos:


<details>
<summary><strong>Haz clic aquí para ver el script completo</strong></summary>

```sql
-- ================================
-- creación de base de datos
-- ================================
drop database if exists ventadecomida;
create database ventadecomida;
use ventadecomida;

-- ================================
-- tablas
-- ================================
create table productos (
    idProducto int auto_increment primary key,
    nombreProducto varchar(64),
    marca varchar(64) not null,
    precio decimal(10,2) not null,
    stock int not null,
    fechaDeCaducidad date
);

create table usuarios (
    idUsuario int auto_increment primary key,
    username varchar(50) not null unique,
    nombreCompleto varchar(100) not null,
    correoElectronico varchar(100) not null unique,
    password varchar(255) not null,
    numeroTelefono varchar(20),
    fechaNacimiento date,
    tipoDeCuenta enum('Administrador', 'Usuario') not null default 'Usuario'
);

create table compras (
    idCompra int auto_increment primary key,
    idUsuario int not null,
    fechaCompra date not null,
    total decimal(10,2) not null,
    foreign key (idUsuario) references usuarios(idUsuario) on delete cascade
);

create table detalle_compras (
    idDetalle int auto_increment primary key,
    idCompra int not null,
    idProducto int not null,
    cantidad int not null,
    precioUnitario decimal(10,2) not null,
    foreign key (idCompra) references compras(idCompra) on delete cascade,
    foreign key (idProducto) references productos(idProducto) on delete cascade
);

create table alertas_stock (
    idAlerta int auto_increment primary key,
    idProducto int,
    mensaje varchar(255),
    fechaAlerta datetime default now()
);

-- ================================
-- triggers
-- ================================
delimiter $$

create trigger tr_restar_stock
after insert on detalle_compras
for each row
begin
    update productos
    set stock = stock - new.cantidad
    where idProducto = new.idProducto;
end $$

create trigger tr_sumar_stock_al_eliminar
after delete on detalle_compras
for each row
begin
    update productos
    set stock = stock + old.cantidad
    where idProducto = old.idProducto;
end $$

delimiter ;

-- ================================
-- procedimientos productos
-- ================================
delimiter $$

create procedure sp_listarProductos()
begin
    select * from productos;
end $$

create procedure sp_agregarProductos(
    in pNombreProducto varchar(64),
    in pMarca varchar(64),
    in pPrecio decimal(10,2),
    in pStock int,
    in pFechaDeCaducidad date
)
begin
    insert into productos (nombreProducto, marca, precio, stock, fechaDeCaducidad)
    values (pNombreProducto, pMarca, pPrecio, pStock, pFechaDeCaducidad);
end $$

create procedure sp_editarProductos(
    in pIdProducto int,
    in pNombreProducto varchar(64),
    in pMarca varchar(64),
    in pPrecio decimal(10,2),
    in pStock int,
    in pFechaDeCaducidad date
)
begin
    update productos
    set nombreProducto = pNombreProducto,
        marca = pMarca,
        precio = pPrecio,
        stock = pStock,
        fechaDeCaducidad = pFechaDeCaducidad
    where idProducto = pIdProducto;
end $$

create procedure sp_eliminarProductos(
    in pIdProducto int
)
begin
    delete from productos
    where idProducto = pIdProducto;
end $$

create procedure sp_buscarProductos(
    in pIdProducto int
)
begin
    select * from productos
    where idProducto = pIdProducto;
end $$

delimiter ;

-- ================================
-- procedimientos usuarios
-- ================================
delimiter //

DELIMITER $$

CREATE PROCEDURE sp_agregar_usuario(
    IN p_username VARCHAR(50),
    IN p_nombreCompleto VARCHAR(100),
    IN p_correoElectronico VARCHAR(100),
    IN p_password VARCHAR(100),
    IN p_numeroTelefono VARCHAR(20),
    IN p_fechaNacimiento DATE,
    IN p_tipoDeCuenta VARCHAR(20)
)
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE username = p_username) THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'El nombre de usuario ya existe.';
    ELSE
        INSERT INTO usuarios (username, nombreCompleto, correoElectronico, password, numeroTelefono, fechaNacimiento, tipoDeCuenta)
        VALUES (p_username, p_nombreCompleto, p_correoElectronico, p_password, p_numeroTelefono, p_fechaNacimiento, p_tipoDeCuenta);
    END IF;
END$$


create procedure sp_listar_usuarios()
begin
    select * from usuarios;
end //

create procedure sp_buscar_usuario_por_id(
    in pIdUsuario int
)
begin
    select * from usuarios where idUsuario = pIdUsuario;
end //

create procedure sp_actualizar_usuario(
    in pIdUsuario int,
    in pUsername varchar(50),
    in pNombreCompleto varchar(100),
    in pCorreoElectronico varchar(100),
    in pPassword varchar(255),
    in pNumeroTelefono varchar(20),
    in pFechaNacimiento date,
    in pTipoDeCuenta enum('Administrador', 'Usuario')
)
begin
    update usuarios set
        username = pUsername,
        nombreCompleto = pNombreCompleto,
        correoElectronico = pCorreoElectronico,
        password = pPassword,
        numeroTelefono = pNumeroTelefono,
        fechaNacimiento = pFechaNacimiento,
        tipoDeCuenta = pTipoDeCuenta
    where idUsuario = pIdUsuario;
end //

create procedure sp_eliminar_usuario(
    in pIdUsuario int
)
begin
    delete from usuarios where idUsuario = pIdUsuario;
end //

create procedure sp_validar_login (
    in pUsername varchar(50),
    in pPassword varchar(255),
    out pResultado int
)
begin
    declare vCount int;

    select count(*) into vCount
    from usuarios
    where username = pUsername and password = pPassword;

    set pResultado = if(vCount > 0, 1, 0);
end //

delimiter ;

-- ================================
-- procedimientos compras
-- ================================
delimiter $$

create procedure sp_agregar_compra(
    in pIdUsuario int,
    in pFechaCompra date,
    in pTotal decimal(10,2)
)
begin
    insert into compras (idUsuario, fechaCompra, total)
    values (pIdUsuario, pFechaCompra, pTotal);
end $$

create procedure sp_listar_compras()
begin
    select c.idCompra, c.idUsuario, u.username, c.fechaCompra, c.total
    from compras c
    join usuarios u on c.idUsuario = u.idUsuario;
end $$

create procedure sp_buscar_compra_por_id(
    in pIdCompra int
)
begin
    select * from compras where idCompra = pIdCompra;
end $$

create procedure sp_actualizar_compra(
    in pIdCompra int,
    in pIdUsuario int,
    in pFechaCompra date,
    in pTotal decimal(10,2)
)
begin
    update compras
    set idUsuario = pIdUsuario,
        fechaCompra = pFechaCompra,
        total = pTotal
    where idCompra = pIdCompra;
end $$

create procedure sp_eliminar_compra(
    in pIdCompra int
)
begin
    delete from compras where idCompra = pIdCompra;
end $$

delimiter ;

-- ================================
-- procedimientos detalle_compras
-- ================================
delimiter $$

create procedure sp_agregar_detalle_compra (
    in pIdCompra int,
    in pIdProducto int,
    in pCantidad int
)
begin
    declare vPrecioUnitario decimal(10,2);
    declare vTotalCompra decimal(10,2);
    declare vTotalConImpuestos decimal(10,2);
    declare vStock int;
    declare vMensaje text;

    select precio, stock into vPrecioUnitario, vStock
    from productos
    where idProducto = pIdProducto;

    if vStock = 0 then
        set vMensaje = 'producto agotado. no se puede agregar al detalle de compra.';
        signal sqlstate '45000' set message_text = vMensaje;
    end if;

    if pCantidad > vStock then
        set vMensaje = concat('stock insuficiente. solo hay ', vStock, ' unidades disponibles.');
        signal sqlstate '45000' set message_text = vMensaje;
    end if;

    --  Guardar solo el precio unitario en la tabla detalle_compras
    insert into detalle_compras (idCompra, idProducto, cantidad, precioUnitario)
    values (pIdCompra, pIdProducto, pCantidad, vPrecioUnitario);

    -- Recalcular total de la compra
    select ifnull(sum(cantidad * precioUnitario), 0) into vTotalCompra
    from detalle_compras
    where idCompra = pIdCompra;

    set vTotalConImpuestos = vTotalCompra * 1.17;

    update compras
    set total = vTotalConImpuestos
    where idCompra = pIdCompra;
end;


create procedure sp_listar_detalles()
begin
    select d.idDetalle, d.idCompra, d.idProducto, p.nombreProducto, d.cantidad, d.precioUnitario
    from detalle_compras d
    join productos p on d.idProducto = p.idProducto;
end $$

create procedure sp_detalles_por_compra(
    in pIdCompra int
)
begin
    select d.idDetalle, d.idCompra, d.idProducto, p.nombreProducto, d.cantidad, d.precioUnitario
    from detalle_compras d
    join productos p on d.idProducto = p.idProducto
    where d.idCompra = pIdCompra;
end $$

create procedure sp_eliminar_detalle_compra(
    in pIdDetalle int
)
begin
    delete from detalle_compras where idDetalle = pIdDetalle;
end $$

create procedure sp_actualizar_detalle_compra_total(
    in pIdDetalle int,
    in pNuevoIdProducto int,
    in pNuevaCantidad int,
    in pNuevoPrecioUnitario decimal(10,2)
)
begin
    declare vIdProductoAnterior int;
    declare vCantidadAnterior int;

    select idProducto, cantidad
    into vIdProductoAnterior, vCantidadAnterior
    from detalle_compras
    where idDetalle = pIdDetalle;

    update productos
    set stock = stock + vCantidadAnterior
    where idProducto = vIdProductoAnterior;

    update detalle_compras
    set idProducto = pNuevoIdProducto,
        cantidad = pNuevaCantidad,
        precioUnitario = pNuevoPrecioUnitario
    where idDetalle = pIdDetalle;

    update productos
    set stock = stock - pNuevaCantidad
    where idProducto = pNuevoIdProducto;

    update compras c
    set c.total = (
        select ifnull(sum(cantidad * precioUnitario), 0)
        from detalle_compras
        where idCompra = (select idCompra from detalle_compras where idDetalle = pIdDetalle)
    )
    where c.idCompra = (select idCompra from detalle_compras where idDetalle = pIdDetalle);
end $$

delimiter ;


-- ================================
-- PRUEBAS
-- ================================

-- Insertar productos
call sp_agregarProductos('CocaCola', 'CocaCola', 17.00, 20, '2025-12-31');
call sp_agregarProductos('Pepsi', 'PepsiCo', 15.50, 15, '2024-06-30');

-- Insertar usuario
call sp_agregar_usuario('fer.m', 'Fernando M.', 'fer@mail.com', '1234', '12345678', '1990-01-01', 'Usuario');
call sp_agregar_usuario('fer', 'Fernando M.', 'fernan@gmail.com', '1', '12345678', '1990-01-01', 'Administrador');
call sp_agregar_usuario('Admin', 'Administrador', 'administrador@gmail.com', '1234', '12345678', '1990-01-01', 'Administrador');
call sp_agregar_usuario('User', 'Usuario', 'usuario@gmail.com', '1234', '12345678', '1990-01-01', 'Usuario');

-- Insertar compra con total inicial 0
call sp_agregar_compra(1, '2025-07-14', 0.00);

-- Insertar detalle sin precio manual, se calcula en el procedimiento
call sp_agregar_detalle_compra(1, 1, 3); -- producto 1, cantidad 3
call sp_agregar_detalle_compra(1, 2, 2); -- producto 2, cantidad 2

-- Listar detalles para verificar
call sp_listar_detalles();

call sp_listarProductos();  
-- call sp_agregar_detalle_compra(1, 1, 15); -- producto 1, cantidad 15 (valida stock)
-- call sp_agregar_detalle_compra(1, 1, 2); -- producto 1, cantidad 15 (valida stock)

SELECT
    c.idCompra,
    c.fechaCompra,
    u.username,
    u.correoElectronico,
    p.nombreProducto,
    d.cantidad,
    d.precioUnitario,
    (d.cantidad * d.precioUnitario) AS subtotal,
    c.total
FROM compras c
JOIN usuarios u ON c.idUsuario = u.idUsuario
JOIN detalle_compras d ON c.idCompra = d.idCompra
JOIN productos p ON d.idProducto = p.idProducto
WHERE c.idUsuario = 1
ORDER BY c.fechaCompra DESC;



