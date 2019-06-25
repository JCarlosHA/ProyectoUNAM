package com.cocay.sicecd.step;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cocay.sicecd.model.Curso;
import com.cocay.sicecd.repo.CursoBatchRep;

public class WriterCurso implements ItemWriter<Curso> {

    @Autowired
    private CursoBatchRep cursoRep;
    
    public WriterCurso(CursoBatchRep cursoRep) {
		this.cursoRep = cursoRep;
	}

    @Override
    public void write(List<? extends Curso> curso) throws Exception {

        System.out.println("Data Saved for Users: " + curso);
        cursoRep.saveAll(curso);
        //cursoRep.save(curso);
    }


}
