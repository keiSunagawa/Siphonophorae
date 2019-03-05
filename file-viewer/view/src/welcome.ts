//import {computedFrom} from 'aurelia-framework';
import { EntryPoint } from './scala/frontend-backend-fastopt'
import { read } from 'fs';

export class Welcome {
  filter: String = ""
  expr: String = ""
  order: String = ""
  loadTable: String[][] = []
  printHeader: String[] = []
  allBody: String[][] = []
  printBody: String[][] = []
  csvFile: any

  fileManager = new FileManager()
  service: any // TODO class annotation

  constructor() {
    this.service = EntryPoint.compile(
      // input
      () => this.loadTable,
      () => {
        console.log(this.filter)
        return this.filter
      },
      () => this.expr,
      () => this.order,
      // output
      (arr: String[][]) => {
        const [h, ...t] = arr
        this.printHeader = h
        this.allBody = t
        this.printBody = this.allBody.slice(0, 100)
      },
      (error: String) => console.log(error),
      (error: String) => console.log(error),
      (error: String) => console.log(error)
    )
  }

  submit() {
    const file = this.csvFile[0]
    this.fileManager.read(file).then((res) => {
      try {
        this.loadTable = res
        this.service.run()
      } catch(err) {
        console.log(err)
      }
    })
  }
}

export class UpperValueConverter {
  toView(value: string): string {
    return value && value.toUpperCase();
  }
}

class FileManager {
  read(file: File) {
    return new Promise<String[][]>((resolve, _) => {
      const fr = new FileReader()
      fr.onload = (event: any) => {
        // TODO error handling?
        const res = event.target.result
        const lines: String[] = res.split('\n').filter((l) => l !== "")
        const table: String[][] = lines.map((l) => l.split(','))
        resolve(table);
      }
      fr.readAsText(file)
    });
  }
}
