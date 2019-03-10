export class CsvLoader {
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
