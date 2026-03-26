import fileIo from '@ohos.file.fs';
import { BusinessError } from '@ohos.base';
import common from '@ohos.app.ability.common';

class FileResult {
  path: string = '';
  content: string = '';
}

export default class CacheFileManager {
  private basePath: string = '';
  context: common.Context | null = null;

  onCreate(): void {
    if (!this.context) return;
    
    this.basePath = this.context.cacheDir + '/files/';

    try {
      fileIo.mkdirSync(this.basePath);
    } catch (e) {}

    const secretDir = this.context.cacheDir + '/secret/';
    try {
      fileIo.mkdirSync(secretDir);
    } catch (e) {}

    this.write(secretDir + 'flag.txt', 'FLAG{path_traversal}');
  }

  private write(path: string, content: string): void {
    try {
      const file = fileIo.openSync(path, fileIo.OpenMode.CREATE | fileIo.OpenMode.WRITE_ONLY);
      fileIo.writeSync(file.fd, content);
      fileIo.closeSync(file.fd);
    } catch (err) {
      // ignore
    }
  }

  private read(path: string): string {
    return fileIo.readTextSync(path);
  }

  query(uri: string, predicates: string, columns: string[], callback: (err: BusinessError | null, result?: FileResult[]) => void): void {
    const file = uri.split('/').pop() as string;

    // VULNERABLE: Path Traversal - no validation of ../ sequences
    // Attack: file = "../secret/flag.txt" escapes directory
    const path = this.basePath + file;
    const result = new FileResult();
    result.path = path;
    
    try {
      result.content = this.read(path);
      callback(null, [result]);
    } catch (err) {
      callback(err as BusinessError);
    }
  }

  insert(uri: string, valueBucket: Record<string, Object>, callback: (err: BusinessError | null) => void): void {
    const file = uri.split('/').pop() as string;
    const content = valueBucket['content'] as string || '';

    // VULNERABLE: Path Traversal - no boundary check
    // Attack: file = "../../../sensitive/inject.txt" creates files outside sandbox
    const path = this.basePath + file;

    try {
      this.write(path, content);
      callback(null);
    } catch (err) {
      callback(err as BusinessError);
    }
  }

  delete(uri: string, predicates: string, callback: (err: BusinessError | null) => void): void {
    const file = uri.split('/').pop() as string;

    // VULNERABLE: Path Traversal - allows directory escape
    // Attack: file = "../secret/flag.txt" deletes files outside sandbox
    const path = this.basePath + file;

    try {
      fileIo.unlinkSync(path);
      callback(null);
    } catch (err) {
      callback(err as BusinessError);
    }
  }

  openFile(uri: string, mode: string, callback: (err: BusinessError | null, result?: FileResult) => void): void {
    const file = uri.split('/').pop() as string;

    // VULNERABLE: Path Traversal - unrestricted file access
    // Attack: file = "../secret/flag.txt" reads arbitrary files
    const path = this.basePath + file;

    try {
      const content: string = fileIo.readTextSync(path);
      const result = new FileResult();
      result.path = path;
      result.content = content;
      callback(null, result);
    } catch (err) {
      callback(err as BusinessError);
    }
  }
}