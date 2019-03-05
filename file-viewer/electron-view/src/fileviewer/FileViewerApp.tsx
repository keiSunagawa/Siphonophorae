import * as React from 'react';

import { FileViewerEntryPoint } from '../scala/frontend-backend-fastopt'
import { Button } from 'react-bootstrap';

export class FileViewerApp extends React.Component {
  handleClick() {
    const service = FileViewerEntryPoint.compile(
      // input
      () => [['aaa', 'bbb'], ["aa", "bb"], ["cc", "dd"]],
      () => {
        return 'aaa = "aa"'
      },
      () => "",
      () => "aaa asc",
      // output
      (arr: String[][]) => {
        console.log(arr)
      },
      (error: String) => console.log(error),
      (error: String) => console.log(error),
      (error: String) => console.log(error)
    )
    service.run()
    console.log("click...")
  }

  render() {
    return (
      <div>
        <h2>FileViewer</h2>
        <Button
          variant="primary"
          onClick={this.handleClick}
        >P</Button>
      </div>
    )
  }
}
