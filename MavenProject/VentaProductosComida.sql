Drop database if exists VentaDeComida;
Create DataBase VentaDeComida ;
use VentaDeComida ;

Create table Productos (
	idProducto  int auto_increment,
    nombreProducto varchar(64) ,
    marca varchar(64) not null,
    precio decimal(10,2) not null,
    stock int(11) not null,
    constraint pk_productos primary key (idProducto)
);


DELIMITER $$
	CREATE PROCEDURE sp_listarProductos()
	BEGIN
		SELECT * FROM Productos;
	END $$
DELIMITER ;

-- Crear procedimiento para insertar un producto
DELIMITER $$

CREATE PROCEDURE sp_AgregarProducto(
    IN p_nombreProducto VARCHAR(64),
    IN p_marca VARCHAR(64),
    IN p_precio DECIMAL(10,2),
    IN p_stock INT
)
BEGIN
    INSERT INTO Productos (nombreProducto, marca, precio, stock)
    VALUES (p_nombreProducto, p_marca, p_precio, p_stock);
END $$

DELIMITER ;

call sp_AgregarProducto("CocaCola","cOCAcolA",17.00,12);
call sp_listarProductos();