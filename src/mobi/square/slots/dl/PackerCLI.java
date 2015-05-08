package mobi.square.slots.dl;

public class PackerCLI {

	public static void main(String[] args) {
		if (args.length < 1) return;
		Packer pack = new Packer();
		if (args[0].equals("-x")) {
			int result = pack.extract(args[1]);
			switch (result) {
				case 1:
					System.out.println("Не передано имя файла");
					break;
				case 2:
					System.out.println("Файл не существует");
					break;
				case 3:
					System.out.println("Ошибка при чтении файла");
					break;
				case 4:
					System.out.println("Ошибка при записи в файл");
					break;
			}
		} else {
			int result = pack.pack(args[0]);
			switch (result) {
				case 1:
					System.out.println("не передано имя папки");
					break;
				case 2:
					System.out.println("объект не является папкой");
					break;
				case 3:
					System.out.println("не удалось создать файл");
					break;
				case 4:
					System.out.println("ошибка при записи в файл");
					break;
			}
		}
	}

}
