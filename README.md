# locale-exporter [WIP]

> DANGER! this is experimental project. do not use production!

## what is this 

globalization resource exporter (google spread sheet -> *.properties or *.js) 

google docs 에 있는 데이터를 properties 나 js 로 export 하는 유틸리티입니다.
국제화시 유용하게 사용될수 있습니다.

## Installation

	$ lein uberjar 

	check your `target` directory


## Usage
	
	$ java -jar locale-exporter.jar <mode> <sheet-id> <file-name>
	  mode : json | properties 
	  sheet-id : your google sheet id 
	  file-name : file name 

## Options

## Examples

### Bugs


## License

Copyright © 2016 Bohyung kim 

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
