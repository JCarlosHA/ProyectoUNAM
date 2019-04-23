package com.cocay.sicecd.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cocay.sicecd.model.Usuario_sys;
import com.cocay.sicecd.repo.Estatus_usuario_sysRep;
import com.cocay.sicecd.repo.Perfil_sysRep;
import com.cocay.sicecd.repo.Usuario_sysRep;
import com.cocay.sicecd.service.SendMailService;


@Controller
public class AltaUsuarios {
	
	@Autowired 
	Usuario_sysRep _usuarioSys;
	@Autowired
	Estatus_usuario_sysRep estatusSys;
	@Autowired
	Perfil_sysRep perfilSys;
	@Autowired
	SendMailService _email;
	
	@RequestMapping(value = "/AdministracionCursos/formAltaUsuario", method = RequestMethod.GET)
	public String formularioAltaUsuario() {
		return "altaUsuario/agregaUsuario";
	}
	
		
	@PostMapping("/AdministracionCursos/altaUsuario")
	public ResponseEntity<String> darAltaUsuario(@RequestBody Usuario_sys consulta) 
	{
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		consulta.setPassword(passwordEncoder.encode(consulta.getPassword()));
		consulta.setFk_id_estatus_usuario_sys(estatusSys.findByNombre("Inactivo").get(0));
		consulta.setFk_id_perfil_sys(perfilSys.findByNombre("Consultas").get(0));
		consulta.setConfirmacion("true");
		String codigo=String.valueOf((int) (Math.random() * 1000) + 1);
		consulta.setCodigoCorreo(codigo);

		_usuarioSys.save(consulta);
		Usuario_sys guardado= _usuarioSys.findByCorreo(consulta.getCorreo()).get(0);
		String link="http://localhost:8080/activacionConsulta?codigo="+codigo+"&usuario="+guardado.getPk_id_usuario_sys();
		String from="cocayprueba@gmail.com";
		String to=consulta.getCorreo();
		String subject="Activación de cuenta";
		String body="Hola da clic al siguiente  link \n" + 
				link+ "\npara activar tu cuenta.";
		_email.sendMail(from, to, subject, body);
		return ResponseEntity.ok("Usuario Agregado con exito");
	}
	
	@RequestMapping(value = "/AdministracionCursos/formAltaAdminstrador", method = RequestMethod.GET)
	public String formularioAltaAdminstrador() {
		return "altaUsuario/agregaAdminstrador";
	}
	
		
	@PostMapping("/AdministracionCursos/altaAdministrador")
	public ResponseEntity<String> darAltaAdministrador(@RequestBody Usuario_sys consulta) 
	{
		consulta.setFk_id_estatus_usuario_sys(estatusSys.findByNombre("Inactivo").get(0));
		consulta.setFk_id_perfil_sys(perfilSys.findByNombre("Administrador").get(0));
		int codigo=(int) (Math.random() * 1000) + 1;
		String link="http://localhost:8080/activacionAdmin?codigo="+String.valueOf(codigo)+"&usuario="+consulta.getPk_id_usuario_sys();
		String from="cocayprueba@gmail.com";
		String to=consulta.getCorreo();
		String subject="Activación de cuenta";
		String body="“Hola da clic al " + 
				"siguiente  link \n"+link+ "\npara activar tu cuenta y configurar una contraseña.";
		_email.sendMail(from, to, subject, body);
		_usuarioSys.save(consulta);
		return ResponseEntity.ok("Usuario Agregado con exito");
	}
	
	@GetMapping("/AdministracionCursos/correo")
	public String envia() {

		return "altaUsuario/agregaUsuario";

		
	}
	
	@GetMapping("/activacionConsulta")
	public String ActivacionConsulta(
			@RequestParam(name = "usuario") int id,
			@RequestParam(name = "codigo") String codigo ) 
	{
		if (_usuarioSys.existsById(id)) {
			Usuario_sys candidato= (_usuarioSys.findById(id)).get();
			if (codigo.equals(candidato.getCodigoCorreo())) {
				candidato.setFk_id_estatus_usuario_sys(estatusSys.findByNombre("Activo").get(0));;
				candidato.setConfirmacion("false");
				_usuarioSys.save(candidato);
			}
			
		}
		
		return "redirect:/login?mensaje=usuarioActivado";
	}
	
	
	
	@GetMapping("/prueba")
	@ResponseBody
	public  List<String> prueba() {
		
		Usuario_sys candidato= (_usuarioSys.findById(5)).get();
		candidato.setConfirmacion("false");
		_usuarioSys.save(candidato);
		List<String> lista =new ArrayList<>();
		lista.add("hola1");
		lista.add("hola2");

		return lista;
	}
}



