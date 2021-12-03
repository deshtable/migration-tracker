import { elementEventFullName } from '@angular/compiler/src/view_compiler/view_compiler';
import { Component } from '@angular/core';
import {FormControl, FormGroup, FormArray } from '@angular/forms';

// interfaces not used, but give idea of data for each step type
interface Get_Data{
  outputTable: string,
  connectionUriPrefix: string,
  host: string,
  port: string,
  database: string,
  jdbcTable: string,
  userName: string,
  password: string,
  jdbcdriver: string,
  jdbcSelectFields: string
}
interface Select_Data{
  outputTable: string,
  queryFileURL: string
}

interface Ingest_Data{
  inputTable: string,
  connectionUriPrefix: string,
  host: string,
  port: string,
  database: string,
  jdbcTable: string,
  userName: string,
  password: string,
  jdbcdriver: string,
  jdbcSelectFields: string
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'configApp';
  header = new FormGroup({
    name: new FormControl(''),
    version: new FormControl('1.0'),
    description: new FormControl(''),
    steps: new FormArray([ 
    ])
  });

  get steps(){
    return this.header.get('steps') as FormArray;
  }

  addStep() {
    this.steps.push(new FormGroup({
        type : new FormControl(''),
        group : new FormGroup({})
    }));
    console.log(this.steps.controls[0].value)

  }
  
  updateArr(i : number){
    let t = this.steps.controls[i].value.type
    if(t == "get_data")
    {
      this.steps.controls[i].value.group = new FormGroup({
        outputTable:  new FormControl(''),
        connectionUriPrefix: new FormControl(''),
        host: new FormControl(''),
        port: new FormControl(''),
        database: new FormControl(''),
        jdbcTable: new FormControl(''),
        userName: new FormControl(''),
        password: new FormControl(''),
        jdbcdriver: new FormControl(''),
        jdbcSelectFields: new FormControl('')
      });
    }
    else if(t == "select_data")
    {
      this.steps.controls[i].value.group = new FormGroup({
        outputTable: new FormControl(''),
        queryFileURL: new FormControl('')
      });
    }
    else if(t == "ingest_data")
    {
      this.steps.controls[i].value.group = new FormGroup({
        inputTable:  new FormControl(''),
        connectionUriPrefix: new FormControl(''),
        host: new FormControl(''),
        port: new FormControl(''),
        database: new FormControl(''),
        jdbcTable: new FormControl(''),
        userName: new FormControl(''),
        password: new FormControl(''),
        jdbcdriver: new FormControl(''),
        jdbcSelectFields: new FormControl('')
      });
    }
    console.log(this.steps)
  }
  
  download(){
    console.log(this.header.value);
    const link = document.createElement('a');
    let stepsText = "";
    for(let i = 0; i < this.steps.length; i++)
    {
      stepsText = stepsText + "\n\t\t\"step" + (i+1) + "\": {\n\t\t\t";
      let t = this.steps.controls[i].value.type;
      let g = this.steps.controls[i].value.group
      stepsText = stepsText + "\"component\": \"" + t.toUpperCase() + "\",\n\t\t\t";
      stepsText = stepsText + "\"componentParameters\": {\n\t\t\t\t"

      if(t == "get_data")
      {
        stepsText = stepsText + "\"outputTable\": \"" + g.value.outputTable + "\",\n\t\t\t\t";
        stepsText = stepsText + "\"connectionConfig\": {\n\t\t\t\t\t";
        stepsText = stepsText + "\"connectionDetails\": {\n\t\t\t\t\t\t";
        stepsText = stepsText + "\"connectionUriPrefix\": \"" + g.value.connectionUriPrefix + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"host\": \"" + g.value.host + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"port\": " + g.value.port + ",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"database\": \"" + g.value.database + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"userName\": \"" + g.value.userName + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"password\": \"" + g.value.password + "\"\n\t\t\t\t\t"
        stepsText = stepsText + "}, \n\t\t\t\t"
        stepsText = stepsText + "\"jdbcDriverClass\": \"" + g.value.jdbcdriver + "\"\n\t\t\t\t"
        stepsText = stepsText + "},\n\t\t\t"
        stepsText = stepsText + "\"jdbcTable\": \"" + g.value.jdbcTable + "\",\n\t\t\t"
        let s = g.value.jdbcSelectFields.replace(/ /g, '');
        s = s.split(",");
        stepsText = stepsText + "\"jdbcSelectFields\": [";
        for(let j = 0; j < s.length; j++)
        {
          stepsText = stepsText + "\""+ s[j] + "\"";
          if(j != s.length-1)
            stepsText = stepsText + ", ";
        }
        stepsText = stepsText + "]\n\t\t\t\t"

      }
      else if(t == "select_data")
      {
        stepsText = stepsText + "\"outputTable\": \"" + g.value.outputTable + "\",\n\t\t\t\t";
        stepsText = stepsText + "\"queryFileURL\": \"" + g.value.queryFileURL + "\"\n\t\t\t\t";
      }
      else if(t == "ingest_data")
      {
        stepsText = stepsText + "\"inputTable\": \"" + g.value.inputTable + "\",\n\t\t\t\t";
        stepsText = stepsText + "\"connectionConfig\": {\n\t\t\t\t\t";
        stepsText = stepsText + "\"connectionDetails\": {\n\t\t\t\t\t\t";
        stepsText = stepsText + "\"connectionUriPrefix\": \"" + g.value.connectionUriPrefix + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"host\": \"" + g.value.host + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"port\": " + g.value.port + ",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"database\": \"" + g.value.database + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"userName\": \"" + g.value.userName + "\",\n\t\t\t\t\t\t"
        stepsText = stepsText + "\"password\": \"" + g.value.password + "\"\n\t\t\t\t\t"
        stepsText = stepsText + "}, \n\t\t\t\t"
        stepsText = stepsText + "\"jdbcDriverClass\": \"" + g.value.jdbcdriver + "\"\n\t\t\t\t"
        stepsText = stepsText + "},\n\t\t\t"
        stepsText = stepsText + "\"jdbcTable\": \"" + g.value.jdbcTable + "\",\n\t\t\t"
        let s = g.value.jdbcSelectFields.replace(/ /g, '');
        s = s.split(",");
        stepsText = stepsText + "\"jdbcSelectFields\": [";
        for(let j = 0; j < s.length; j++)
        {
          stepsText = stepsText + "\""+ s[j] + "\"";
          if(j != s.length-1)
            stepsText = stepsText + ", ";
        }
        stepsText = stepsText + "]\n\t\t\t\t"
      }
      stepsText = stepsText +  "\n\t\t\t}";
      stepsText = stepsText + "\n\t\t}"
      if(i != this.steps.length-1)
        stepsText = stepsText + ","
    }


    link.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(
    //Here we create the config.json file
      "{\n\t\"name\": \"" + this.header.value.name + "\"," +
      "\n\t\"version\": \"" + this.header.value.version + "\"," +
      "\n\t\"description\": \"" + this.header.value.description + "\"," +
      "\n\t\"workflow\": {" +
      stepsText +
      "\n\t}" + 
      "\n}" 
    ));
    link.setAttribute('download', "config.json");
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

  }

  removeStep() {
    this.steps.removeAt(this.steps.length-1);
  }

}



