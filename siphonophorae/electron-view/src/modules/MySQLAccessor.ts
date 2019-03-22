import * as mysql from 'mysql'
import * as fs from 'fs'
import * as yaml from 'js-yaml'
import * as moment from 'moment'
import * as R from 'ramda'

interface Connection {
  host: string,
  database: string,
  user: string,
  password: string
}
interface ResultTable {
  header: String[]
  body: String[][]
}
interface Table {
  name: string
  colmns: string[]
}
export class MySQLAccessor {
  conf: Connection
  constructor(confPath: string) {
    this.conf = yaml.safeLoad(fs.readFileSync(confPath, 'utf8'));
  }

  schemaInfo(): Promise<Table[]> {
    const sql = `select table_name, column_name from columns where table_schema = '${this.conf.database}';`
    const infoConf = { ...this.conf, database: "information_schema" }
    return new Promise((resolve, reject) => {
      const connection = mysql.createConnection(infoConf);
      connection.connect()
      connection.query({ sql: sql }, (error, results) => {
        if (error) { reject(error.message) }
        else {
          const gs = R.groupBy<any>((res) => res.table_name)(results)
          const res = R.reduce<string, Table[]>((acm, key) => {
            const x: Table = { name: key, colmns: gs[key].map((o) => o.column_name) }
            return R.concat(acm, [x])
          }, [])(Object.keys(gs))
          resolve(res)
        }
    })
    connection.end();
    })
  }
  send(query: String): Promise<ResultTable> {
    return new Promise((resolve, reject) => {
      const connection = mysql.createConnection(this.conf);
      connection.connect()
      connection.query({ sql: <string> query, nestTables: '.' }, (error, results, fields) => {
        if (error) { reject(error.message) }
        else {

          const header = fields.map((v) => {
            const table = v.orgTable ? `${v.orgTable}` : ""
            return `${table}.${v.orgName ? v.orgName : v.name}`
          })
          const body = results.map((b) => header.map((h) => {
            const v = b[h]
            if (v == null || v == undefined) {
              return ""
            } else if (typeof v == "object") {
              if (v instanceof Date) {
                return moment(v).format("YYYY-MM-DDZ")
              } else {
                return v.toString()
              }
            } else {
              return v
            }
          }))

          resolve({ header, body })
        }
      })
      connection.end();
    })
  }
}
