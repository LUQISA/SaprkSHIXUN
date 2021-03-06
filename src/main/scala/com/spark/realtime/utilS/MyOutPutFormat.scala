//package com.spark.realtime.utilS
//
//import org.apache.hadoop.fs.FileSystem
//import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
//
///**
//  * @created by imp ON 2019/2/21
//  */
//class MyOutPutFormat extends TextOutputFormat {
//  public RecordWriter getRecordWriter ( FileSystem fs, final JobConf job, final String name, final Progressable arg3) throws IOException {
//
//    return new RecordWriter() {
//
//      TreeMap<String, RecordWriter<K, V>> recordWriters = new TreeMap();
//
//      public void write(Object key, Object value) throws IOException {
//
//        //key中为消息内容，value无意义
//        String line = key.toString();
//
//        //根据消息内容，定义输出路径和输出内容（同时清洗数据）
//        String[] ss = LineInterpret.interpretLine(line, "/test/spark/kafka");
//
//        if (ss != null && ss.length == 2) {
//
//          //name的最后两位为jobid，同一个文件只能同时允许一个job写入，多个job写一个文件会报错，将jobid作为文件名的一部分
//          //能解决此问题
//          String finalPath = ss[1] + "-" + name.substring(name.length() - 2);
//
//          RecordWriter rw = (RecordWriter) this.recordWriters.get(finalPath);
//
//          try {
//
//            if (rw == null) {
//
//              rw = getBaseRecordWriter(fs, job, finalPath, arg3);
//
//              this.recordWriters.put(finalPath, rw);
//            }
//
//            rw.write(ss[0], null);
//
//          } catch (Exception e) {
//            //一个周期内，job不能完成，下一个job启动，会造成同时写一个文件的情况，变更文件名，添加后缀
//            this.rewrite(finalPath + "-", ss[0]);
//          }
//        }
//      }
//
//      public void rewrite(String path, String line) {
//
//        String finalPath = path + new Random().nextInt(10);
//
//        RecordWriter rw = (RecordWriter) this.recordWriters.get(finalPath);
//
//        try {
//
//          if (rw == null) {
//
//            rw = getBaseRecordWriter(fs, job, finalPath, arg3);
//
//            this.recordWriters.put(finalPath, rw);
//          }
//
//          rw.write(line, null);
//
//        } catch (Exception e) {
//          //重试
//          this.rewrite(finalPath, line);
//        }
//      }
//
//      public void close(Reporter reporter) throws IOException {
//        Iterator keys = this.recordWriters.keySet().iterator();
//
//        while (keys.hasNext()) {
//          RecordWriter rw = (RecordWriter) this.recordWriters.get(keys.next());
//          rw.close(reporter);
//        }
//
//        this.recordWriters.clear();
//      }
//    };
//  }
//
//
//  protected RecordWriter<K, V> getBaseRecordWriter(FileSystem fs, JobConf job, String path, Progressable arg3) throws IOException {
//    if (this.theTextOutputFormat == null) {
//      this.theTextOutputFormat = new MyTextOutputFormat();
//    }
//
//    return this.theTextOutputFormat.getRecordWriter(fs, job, path, arg3);
//  }
//}
//}
