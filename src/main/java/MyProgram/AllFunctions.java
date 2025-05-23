/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MyProgram;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author LUCAS FERNANDO
 */
public class AllFunctions {
    //Login
    public static boolean esAdmin(String usuario) {
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                
                // Verificar que la línea tenga datos y que el usuario coincida
                if (datos.length > 0 && datos[4].equals(usuario)) {
                    // Verificar si la columna 10 (índice 9) existe y es "1"
                    return datos.length > 9 && datos[9].equals("1");
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        
        return false;
    }  
    
    public static boolean verificarYActualizarAcceso(String usuario, String clave) {
        File file = new File("Datos.csv");
        if (!file.exists()) {
            return false; // El archivo no existe
        }

        List<String> lineas = new ArrayList<>();
        boolean credencialesCorrectas = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";");
            
                // Verificar credenciales (campos 4 y 5)
                if (campos.length >= 6) {
                    String usuarioDato = campos[4].trim();
                    String claveDato = campos[5].trim();
                
                    if (usuarioDato.equals(usuario) && claveDato.equals(clave)) {
                        credencialesCorrectas = true;
                    
                        // Si hay campo de acceso (posición 10), actualizarlo a "1"
                        if (campos.length >= 11) {
                            campos[10] = "1";
                            linea = String.join(";", campos);
                        }
                    }
                }
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error de lectura - " + e.getMessage());
            return false;
        }

        // Solo escribir si las credenciales fueron correctas
        if (credencialesCorrectas) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String l : lineas) {
                    bw.write(l);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error de escritura - " + e.getMessage());
            }
        }

        return credencialesCorrectas;
    }
       
    public static String[] recuperarCuenta(String correoBuscado){
    try {
        File file = new File("Datos.csv");
        if (!file.exists()) {
            return null; // Si el archivo no existe, no hay registros
        }
        
        FileReader fr = new FileReader("Datos.csv");
        BufferedReader entrada = new BufferedReader(fr);
        String linea;
        
        while ((linea = entrada.readLine()) != null) {
            String[] campos = linea.split(";");
            if (campos.length >= 6) { // Asegurarnos que hay suficientes campos
                String correoExistente = campos[1]; // Índice 1 para correo
                
                if (correoExistente.equalsIgnoreCase(correoBuscado.trim())) {
                    entrada.close();
                    return campos; // Retornamos todos los campos del usuario
                }
            }
        }
        entrada.close();
    } catch (java.io.FileNotFoundException fnfex) {
        System.out.println("Archivo no encontrado: " + fnfex);
    } catch (java.io.IOException ioex) {
        System.out.println("Error de lectura: " + ioex);
    }
    return null;
    }
    
    //Register
    public static void guardarRegistro(String dato, boolean append) {
        try {
            FileWriter fw = new FileWriter("Datos.csv", append);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter salida = new PrintWriter(bw);
            salida.println(dato);
            salida.close();
            } catch (java.io.IOException ioex) {
                System.out.println("Error de escritura: " + ioex);
            }
    }
    
    public static boolean existeRegistro(String Usuario, String Correo){
    try {
        File file = new File("Datos.csv");
        if (!file.exists()) {
            return false; // Si el archivo no existe, no hay registros
        }
        
        FileReader fr = new FileReader("Datos.csv");
        BufferedReader entrada = new BufferedReader(fr);
        String linea;
        
        while ((linea = entrada.readLine()) != null) {
            String[] campos = linea.split(";");
            if (campos.length >= 6) { // Asegurarnos que hay suficientes campos
                String usuarioExistente = campos[4]; // Índice 4 para usuario
                String correoExistente = campos[1];  // Índice 1 para correo
                
                if (usuarioExistente.equals(Usuario)) {
                    entrada.close();
                    return true;
                }
                if (correoExistente.equals(Correo)) {
                    entrada.close();
                    return true;
                }
            }
        }
        entrada.close();
    } catch (java.io.FileNotFoundException fnfex) {
        System.out.println("Archivo no encontrado: " + fnfex);
    } catch (java.io.IOException ioex) {
        System.out.println("Error de lectura: " + ioex);
    }
    return false;
    }
    
    //Dineros
    public static boolean EnviarDinero(int valor, String destino) {
        List<String> lineas = new ArrayList<>();
        String usuarioOrigenId = "";
        String usuarioOrigenNombre = "";
        String usuarioDestinoNombre = "";
        boolean[] encontrados = {false, false}; // [origen, destino]

        try {
            // Leer todo el archivo primero
            try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    lineas.add(linea);
                }
            }

            // Procesar transferencia
            for (int i = 0; i < lineas.size(); i++) {
                String[] datos = lineas.get(i).split(";");
            
                // Usuario origen (sesión activa)
                if (datos.length > 10 && datos[10].equals("1")) {
                    int saldo = Integer.parseInt(datos[6]);
                    saldo -= valor;
                    datos[6] = String.valueOf(saldo);
                    usuarioOrigenId = datos[4]; // ID para transferencia
                    usuarioOrigenNombre = datos[0]; // Nombre real para notificación
                    encontrados[0] = true;
                }
            
                // Usuario destino
                if (datos.length > 4 && datos[4].equals(destino)) {
                    int saldoDestino = Integer.parseInt(datos[6]);
                    saldoDestino += valor;
                    datos[6] = String.valueOf(saldoDestino);
                    usuarioDestinoNombre = datos[0]; // Nombre real del destino
                    encontrados[1] = true;
                }
            
                lineas.set(i, String.join(";", datos));
            }

            // Escribir cambios en Datos.csv
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Datos.csv"))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            // Registrar notificaciones
            registrarMovimientos(destino, String.format("Recibiste $%,d de %s", valor, usuarioOrigenNombre));
        
            registrarMovimientos(usuarioOrigenId, String.format("Transferiste $%,d a %s", valor, usuarioDestinoNombre));

            return true;

        } catch (IOException e) {
            System.err.println("Error en operación de archivo: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Error en formato de números: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean ValidarSaldoSuficiente(int valor) {
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1")) {
                    int saldo = Integer.parseInt(datos[6]);
                    return saldo >= valor;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }

    public static boolean ExisteUsuario(String usuario) {
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 1 && datos[4].equals(usuario)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }

    public static boolean EsUsuarioActual(String usuario) {
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1") && datos.length > 1 && datos[4].equals(usuario)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean HaySaldo(){
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 0 && datos[10].equals("1")){
                    int saldo = Integer.parseInt(datos[6]);
                    return datos.length > 0 && saldo != 0;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }
        
    public static boolean ValidarIngreso(int valor) {
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1")) {
                    int saldo = Integer.parseInt(datos[6]);
                    return saldo >= valor;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean ValidarRetiro(int valor) {
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1")) {
                    int colchon = Integer.parseInt(datos[8]);
                    return colchon >= valor;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }
    
    public static void Ingreso(int valor) {
        List<String> lineasModificadas = new ArrayList<>();
        boolean operacionRealizada = false;

        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1")) {
                    int saldo = Integer.parseInt(datos[6]);
                    int colchon = Integer.parseInt(datos[8]);
                    saldo -= valor;
                    colchon += valor;
                    datos[6] = String.valueOf(saldo);
                    datos[8] = String.valueOf(colchon);
                    operacionRealizada = true;
                }
                lineasModificadas.add(String.join(";", datos));
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        if (operacionRealizada) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Datos.csv"))) {
                for (String nuevaLinea : lineasModificadas) {
                    bw.write(nuevaLinea);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo: " + e.getMessage());
            }
        }
    }
    
    public static void Retiro(int valor) {
        List<String> lineasModificadas = new ArrayList<>();
        boolean operacionRealizada = false;

        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1")) {
                    int saldo = Integer.parseInt(datos[6]);
                    int colchon = Integer.parseInt(datos[8]);
                    saldo += valor;
                    colchon -= valor;
                    datos[6] = String.valueOf(saldo);
                    datos[8] = String.valueOf(colchon);
                    operacionRealizada = true;
                }
                lineasModificadas.add(String.join(";", datos));
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        if (operacionRealizada) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Datos.csv"))) {
                for (String nuevaLinea : lineasModificadas) {
                    bw.write(nuevaLinea);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo: " + e.getMessage());
            }
        }
    }
        
    public static boolean HayColchon(){
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 0 && datos[10].equals("1")){
                    int colchon = Integer.parseInt(datos[8]);
                    return datos.length > 0 && colchon != 0;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }

    //Menu
    public static boolean A_Que_Pag(){
        String linea;
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 10 && datos[10].equals("1")){
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }
    
    //Movimientos
    public static String getUsuario(){
        String linea;
        String usuario = "";
        
        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            if ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";"); 
                if (datos.length >= 10 && datos[10].equals("1")) {
                    usuario = datos[4];
                } else {
                    System.err.println("Error: El CSV no tiene suficientes campos.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return usuario;
    }
    
    private static void registrarMovimientos(String usuarioId, String mensaje) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String registro = String.format("%s;%s;%s%n", usuarioId, fecha, mensaje);
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Movimientos.csv", true))) {
            bw.write(registro);
        } catch (IOException e) {
            System.err.println("Error al registrar notificación: " + e.getMessage());
        }
    }
    
    //Eventos
    public static String registrarEvento(String titulo, String descripcion, int valor) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String registro = String.format("%s;%s;%d;%s%n", titulo, descripcion, valor, fecha);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Notificaciones.csv", true))) {
            bw.write(registro);
            return "Evento registrado";
        } catch (IOException e) {
            System.err.println("Error al guardar el evento: " + e.getMessage());
        }
        return "Algo ha fallado";
    }
    
    public static String eliminarEvento(String titulo) {
        List<String> lineasMantener = new ArrayList<>();
        boolean eventoEncontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader("Notificaciones.csv"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 0 && !datos[0].equalsIgnoreCase(titulo)) {
                    lineasMantener.add(linea);
                } else {
                    eventoEncontrado = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        if (eventoEncontrado) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Notificaciones.csv"))) {
                for (String linea : lineasMantener) {
                    bw.write(linea);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error al escribir en el archivo: " + e.getMessage());
            }
            return "Evento " + titulo + " eliminado correctamente.";
        } else {
            return "Evento " + titulo + " no encontrado.";
        }
    }

    public static boolean Recompensa(String destino, int valor) {
        final String UNAB_NOMBRE = "UNAB";
        String usuarioId = "";
        boolean usuarioEncontrado = false;

        try {
            // Validación básica
            if (valor <= 0) {
                JOptionPane.showMessageDialog(null, "El monto debe ser positivo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Leer y procesar Datos.csv
            List<String> lineas = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(";");

                    if (datos.length > 10 && datos[4].equals(destino)) {
                        int saldoActual = Integer.parseInt(datos[6]);
                        saldoActual += valor; 
                        datos[6] = String.valueOf(saldoActual);
                        usuarioId = datos[4];
                        usuarioEncontrado = true;
                        linea = String.join(";", datos);
                    }
                    lineas.add(linea);
                }
            }

            if (!usuarioEncontrado) {
                JOptionPane.showMessageDialog(null, "No se encontró usuario activo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Escribir cambios
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Datos.csv"))) {
                for (String linea : lineas) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            // Registrar movimiento
            registrarMovimientos(usuarioId, String.format("Depósito de $%,d desde %s", valor, UNAB_NOMBRE));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al procesar archivos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en formato numérico", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    //Cerrar Sesion
    public static boolean CerrarSesion() {
        List<String> lineasModificadas = new ArrayList<>();
        boolean sesionCerrada = false;

        try (BufferedReader br = new BufferedReader(new FileReader("Datos.csv"))) {
            String linea;
            // Leer todas las líneas del archivo
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 10 && datos[10].equals("1")) {
                    // Cambiar el estado de sesión a 0 (cerrado)
                    datos[10] = "0";
                    sesionCerrada = true;
                }
                lineasModificadas.add(String.join(";", datos));
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return false;
        }

        // Solo escribir si encontramos y modificamos la sesión activa
        if (sesionCerrada) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Datos.csv"))) {
                for (String nuevaLinea : lineasModificadas) {
                    bw.write(nuevaLinea);
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo: " + e.getMessage());
                return false;
            }
        }
    
        return false;
    }
}
