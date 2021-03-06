/*
*@Class : UserManager
*@Date : 2020. 09. 12
*@Author : 문지연, 임소희, 백희승
*@Desc : 회원 및 관리자의 로그인 후 ManageSystem 객체의 각각의 함수로 이동
*/

package kr.or.bit_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserManager{

	private ManageSystem manageSystem;
	private File fd = new File("/Users/sunwoo/UserMgData/");
	private Scanner scan = new Scanner(System.in);

	public UserManager(){
		manageSystem = new ManageSystem();
	}

	public void startProcess(){	// 프로그램의 처음 시작지점 startProcess()
		while (true){
			
			try{
				
				System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
				System.out.println("*     마 함 빌리조 자전거 프로그램에 오신 것을 환영합니다       *");
				System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
				System.out.println("==============회원 구분을 선택해 주세요.==============");
				System.out.println("1.회원가입      2.로그인     3.관리자 로그인    4.회원 비밀번호 변경    5.프로그램 종료하기");
				System.out.print("메뉴선택>>");
				String input = scan.nextLine();
				switch (input){
				case "1":
					regist();
					break;
				case "2":
					signIn();
					break;
				case "3":
					signInAsAdmin();
					break;
				case "4":
					changePwd();
					break;
				case "5":
					System.out.println("프로그램을 종료합니다.");
					return;
				default:
					System.out.println("올바른 값을 입력해주세요");
				}
			}catch (IOException e){
				
				System.out.println(e + " 오류발생");
			}catch (Exception e){
				
				System.out.println(e + " 오류발생");
			}
		}
	} // startProcess()


	private void regist() throws IOException{ // 회원가입 regist()
		if (!fd.exists())fd.mkdir();

		System.out.println("아이디는 영문으로 시작하며, 영문과 숫자로 이러우진 5-12자리의 형식이어야 합니다.");
		System.out.println("아이디를 입력해주세요.");
		System.out.print("아이디 : ");
		String id = scan.nextLine().trim().toLowerCase();
		if (isCheckRegex("id", id)){
			
			System.out.println();
			System.out.println("비밀번호는 영문 소,대문자,숫자를 최소 한 개씩 포함한 8-20자리의 형식이어야 합니다.");
			System.out.println("비밀번호를 입력해주세요");
			System.out.print("비밀번호 : ");
			String password = scan.nextLine().trim();
			if (isCheckRegex("password", password)){
				
				System.out.println();
				System.out.println("이름은 한글 2-4자의 형식이어야 합니다.");
				System.out.print("이름 : ");
				String userName = scan.nextLine().trim();
				if (isCheckRegex("username", userName)){
					
					System.out.println();
					System.out.println("생년월일은 yymmdd의 형식이어야 합니다. ex.930814");
					System.out.print("생년월일 : ");
					String birth = scan.nextLine().trim();
					if (isCheckRegex("birth", birth)){
						
						File f = new File(fd.getAbsolutePath() + "/" + id + ".txt");
						if (!f.exists()){
							
							FileWriter fw = new FileWriter(f);
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(password + ":");
							bw.write(userName + ":");
							bw.write(birth);

							bw.close();
							System.out.println(userName + "님의 회원정보가 저장되었습니다.");
							showResult(id, userName, birth);
						} else
						{
							System.out.println("입력하신 [" + id + "] 아이디는 존재합니다.");
							System.out.println("초기화면으로 돌아갑니다.");
							System.out.println();
						}
					}
				}
			}
		}
	} // regist()

	private void showResult(String id, String username, String birth){
		System.out.println();
		System.out.println("*****************************************************************");
		System.out.println("\t아이디 : " + id + ", \t이름 : " + username + ",\t 생년월일 : " + birth);
		System.out.println("*****************************************************************");
		System.out.println();
	}
	
	/*
	* @method Name : isCheckRegex
	* @date : 2020. 09. 12
	* @author : 문지연, 백희승
	* @description : 각 입력값(id, password, userName, birth)을 정규표현식을 통해 검증한다. 
	* @param spec : String type, String info
	* @return : boolean
	*/
	private boolean isCheckRegex(String type, String info){
		boolean check = true;
		String regex = "";
		switch (type){
		
		case "id":
			regex = "^[a-z]{1}([a-z][0-9]){4,11}$";
			break;
		case "password":
			regex = "^([0-9])([a-z])([A-Z]).{8,20}$";
			break;
		case "username":
			regex = "^[가-힣]{2,4}$";
			break;
		case "birth":
			regex = "^[0-9]{2}[0-1]{1}[0-9]{1}[0-3]{1}[0-9]{1}$";
			break;
		}
		
		boolean isMatch = Pattern.matches(regex, info);
		if (!isMatch){
			
			System.out.println("올바르지 않은 형식의 " + type + "입니다. 다시 시도해주세요.");
			System.out.println();
			check = false;
		}
		
		return check;
	}

	private void signIn() throws IOException{ // 회원 로그인 signIn()

		FileReader fr = null;
		BufferedReader br = null;
		String cmp_data;	
		String[] txt_data;	// txt파일에 쓰여진 User의 정보를 읽어 각 값을 split()을 통해 비교할 값을 할당
		File file;			// id이름과 동일한 파일 객체 선언

		System.out.println("로그인을 하기 위한 아이디 / 비밀번호를 입력해주세요.");
		System.out.println("아이디:");
		String id = scan.nextLine().trim().toLowerCase();
		System.out.println();

		file = new File(fd.getAbsolutePath() + "/" + id + ".txt");

		if (file.exists()){
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			cmp_data = br.readLine();
			txt_data = cmp_data.split(":");

			br.close();

			for (int cnt = 3; cnt > 0; cnt--){
				
				System.out.println("비밀번호:");
				String password = scan.nextLine().trim();
				System.out.println();

				if (txt_data[0].equals(password)){
					String userName = txt_data[1];
					String birth = txt_data[2];
					System.out.println(userName + "님 로그인 되었습니다.");
					manageSystem.showMenuRental(id);
					return;
					
				}else{
					if (cnt > 1){
						System.out.println("입력하신 아이디와 비밀번호가 일치하지 않습니다.");
						System.out.print((cnt - 1) + "번의 기회가 남았습니다.\n ");
						
					} else{
						
						System.out.println("입력하신 아이디와 비밀번호가 일치하지 않습니다.");
						System.out.println("초기화면으로 돌아갑니다.");
						System.out.println();
					}
				}
			}
		}
	} // signIn()

	private void signInAsAdmin() throws IOException{	
		Scanner sc = new Scanner(System.in);
		System.out.println("관리자 로그인을 원하시면 아이디/비밀번호를 입력해주세요.");
		System.out.println("아이디:");
		String id = sc.nextLine().trim().toLowerCase();
		System.out.println("비밀번호:");
		String password = sc.nextLine().trim();

		Admin admin = Admin.getInstance();
		if ((id.equals(admin.getId())) && (password.equals(admin.getPwd()))){
			System.out.println("관리자님 로그인 되었습니다.");
			manageSystem.showMenuManage();
			
		}else{
			
			System.out.println("아이디와 비밀번호가 일치하지 않습니다.");
			System.out.println("초기화면으로 돌아갑니다.");
			return;
		}
	}

	private void changePwd() throws IOException{ // getUserInfo()로부터 User객체를 받아 해당 객체 정보 변경
		User user = getUserInfo();
		File file;
		String password;

		if (user != null){
			System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
			System.out.println("*\t\t" + user.getId() + "님의 비밀번호는는 " + user.getPassword() + " 입니다. *");
			System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
			System.out.println("\t\t변경할 비밀번호를 입력하여 주십시요.");
			System.out.println("비밀번호는 영문 소,대문자,숫자를 최소 한 개씩 포함한 8-20자리의 형식");
			System.out.print("\t\t변경할 비밀번호 : ");
			password = scan.nextLine().trim();
			Pattern pwdPattern2 = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");
			Matcher matcher5 = pwdPattern2.matcher(password);
			
			if (!matcher5.matches()){
				System.out.println("올바르지 않은 형식의 비밀번호입니다. 다시 시도해주세요.");
				System.out.println();
				changePwd();
				
			} else if (matcher5.matches()){
				
				file = new File(fd.getAbsolutePath() + "/" + user.getId() + ".txt");
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(password + ":");
				bw.write(user.getUserName() + ":");
				bw.write(user.getBirth());
				bw.close();
			}else{
				
				return;
			}
			System.out.println();

		} else{
			
			return;
		}

		System.out.println(user.getUserName() + "님의 변경된 비밀번호는" + password + "입니다. 다시 로그인 해 주세요!");

	}

	private User getUserInfo() throws IOException{	// id를 검색하여 해당 id가 존재하면 return User
		User user;
		File file;

		FileReader fr = null;
		BufferedReader br = null;
		String cmp_data;

		System.out.println("검색할 아이디를 입력해주세요.");
		System.out.print("아이디 : ");
		String str_id = scan.nextLine().trim().toLowerCase();
		System.out.println();

		file = new File(fd.getAbsolutePath() + "/" + str_id + ".txt");

		if (file.exists()) {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			cmp_data = br.readLine();
			String[] txt_data = cmp_data.split(":");
			br.close();

			for (int cnt = 3; cnt > 0; cnt--) {

				System.out.println("기존 비밀번호를 입력해주세요.");
				String str_password = scan.nextLine().trim(); 
				System.out.println();
				if (txt_data[0].equals(str_password)){
					String str_userName = txt_data[1];
					String str_birth = txt_data[2];
					user = new User(str_id, str_password, str_userName, str_birth);
					return user;
					
				} else{
					
					if (cnt > 1){
						System.out.println("입력하신 비밀번호가 아이디와 일치하지 않습니다.");
						System.out.print((cnt - 1) + "번의 기회가 남았습니다. ");
						
					} else{
						
						System.out.println("입력하신 비밀번호가 아이디와 일치하지 않습니다.");
						System.out.println("초기화면으로 돌아갑니다.");
						System.out.println("======================================");
						System.out.println();
					}
				}
			}
		} else{
			
			System.out.println("입력해주신 아이디" + str_id + "이(가) 존재하지 않습니다.");
		}
		return null;
	}
}
