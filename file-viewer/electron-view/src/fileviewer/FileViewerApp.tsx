import * as React from 'react';

import { FileViewerEntryPoint } from '../scala/frontend-backend-fastopt'
import { TextInput, FileInput } from '../components/input'
import { CsvLoader } from '../modules/FileLoader'
import { Button, Table, Form } from 'react-bootstrap';

interface FileViewerState {
  rawTable: String[][]
  viewHeader: String[]
  viewBody: String[][]
  filter: string
  order: string
  expr: string
}
export class FileViewerApp extends React.Component<{}, FileViewerState> {
  service: any
  readonly fileLoader: CsvLoader = new CsvLoader

  constructor(props: {}) {
    super(props)
    // initialize state
    this.state = {
      rawTable: [[]],
      viewHeader: [],
      viewBody: [[]],
      filter: "",
      order: "",
      expr: ""
    }

    // compile service
    this.service = FileViewerEntryPoint.compile(
      // input
      () => this.state.rawTable,
      () => this.state.filter,
      () => this.state.expr,
      () => this.state.order,
      // output
      (arr: String[][]) => {
        const [h, ...t] = arr
        // TODO move Table component
        const t2 = t.slice(0, 100)
        this.setState({ rawTable: arr, viewHeader: h, viewBody: t2 })
      },
      (error: String) => console.log(error),
      (error: String) => console.log(error),
      (error: String) => console.log(error)
    )

    // bind this for handlars
    this.handleClick = this.handleClick.bind(this)
    this.handleFilterForm = this.handleFilterForm.bind(this)
    this.handleOrderForm = this.handleOrderForm.bind(this)
    this.handleExprForm = this.handleExprForm.bind(this)
    this.handleFileForm = this.handleFileForm.bind(this)
  }

  handleClick() {
    this.service.run()
  }

  handleFilterForm(value: string) {
    this.setState({ filter: value })
  }
  handleOrderForm(value: string) {
    this.setState({ order: value })
  }
  handleExprForm(value: string) {
    this.setState({ expr: value })
  }
  handleFileForm(value: File) {
    this.fileLoader.read(value).then((res) => {
      this.setState({ rawTable: res })
    })
  }

  render() {
    return (
      <div>
        <h2>FileViewer</h2>
        <Form>
          <TextInput
            id="orderInput"
            label="Order"
            handler={this.handleOrderForm}
            placeholder="x asc"
          ></TextInput>
          <TextInput
            id="filterInput"
            label="Filter"
            handler={this.handleFilterForm}
            placeholder="x >= 1"
          ></TextInput>
          <TextInput
            id="exprInput"
            label="Expr"
            handler={this.handleExprForm}
            placeholder="x + y = z"
          ></TextInput>
          <FileInput
            id="fileInput"
            label="File"
            handler={this.handleFileForm}
          ></FileInput>
        </Form>
        <Button
          variant="primary"
          onClick={this.handleClick}
        >Read</Button>
        <ItemTable
          header={this.state.viewHeader}
          body={this.state.viewBody}
        ></ItemTable>
      </div>
    )
  }
}

interface TableProps {
  header: String[]
  body: String[][]
}
const ItemTable = (props: TableProps) => (
    <Table responsive bordered hover size="sm">
      <thead>
        <tr>
          <th></th>
          {props.header.map((h: string) => <th>{h}</th>)}
        </tr>
      </thead>
      <tbody>
        {props.body.map((b: string[], i: number) => {
           return (
             <tr>
               <th>{i+1}</th>
               {b.map((bc: string) => <td>{bc}</td>)}
             </tr>
           )
        })}
      </tbody>
    </Table>
)
