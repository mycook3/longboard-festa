package com.example.trx.service.user;

import com.example.trx.apis.user.dto.ParticipantCreateRequest;
import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.user.Gender;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.user.ParticipantRepository;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ParticipantService {

      private final ParticipantRepository participantRepository;
      private final ContestEventRepository contestEventRepository;

        @Transactional
      public int readExcel() throws IOException {
          int count = 0;
          ClassPathResource resource = new ClassPathResource("data.xlsx");
          File file = resource.getFile();
          String filePath = "C:\\Users\\mycoo\\Documents\\카카오톡 받은 파일\\data.xlsx";
          try (FileInputStream fis = new FileInputStream(file);
               Workbook workbook = new XSSFWorkbook(fis)) {

              SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 원하는 날짜 포맷 지정
              DataFormatter formatter = new DataFormatter();

              Sheet sheet = workbook.getSheetAt(1); // 두 번째 시트
              int rowCount = sheet.getPhysicalNumberOfRows();

              // 첫 행(헤더) 건너뛰기
              for (int i = 1; i < rowCount; i++) {
                  Row row = sheet.getRow(i);
                  if (row == null) continue;

                  ParticipantCreateRequest data = ParticipantCreateRequest.builder()
                          .nameKr(getCellValue(row.getCell(1))) // 이름
                          .birth(getCellValue(row.getCell(2))) // 생년월일
                          .gender(getCellValue(row.getCell(3))) // 성별
                          .phone(getCellValue(row.getCell(4))) // 핸드폰 번호
                          .email(getCellValue(row.getCell(5))) // 이메일 주소
                          .residence(getCellValue(row.getCell(6))) // 거주 지역
                          .oneLiner(getCellValue(row.getCell(7))) // 각오
                          .division(getCellValue(row.getCell(9)).split(" ")[0].toUpperCase()) // 참가 부문
                          .eventToParticipate(extractUpper(getCellValue(row.getCell(10)))) // 참가 종목
                          .emergencyContact(getCellValue(row.getCell(13))) // 비상 연락처
                          .build();

                  createParticipantAndParticipate(data);
                  count++;
              }
          }

          return count;
      }

      public static List<String> extractUpper(String input) {
        Pattern p = Pattern.compile("[A-Za-z]+(?:\\s+[A-Za-z]+)*");
        Matcher m = p.matcher(input);
        List<String> out = new ArrayList<>();
        while (m.find()) {
            String upper = m.group().toUpperCase().replaceAll("\\s+", "_"); // ← 공백 제거
            out.add(upper);
        }

        return out;
      }

      private String getCellValue(Cell cell) {
        if (cell == null) return "";

        CellType cellType = cell.getCellType();

        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        if (cellType == CellType.NUMERIC) {
            if(DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                LocalDate localDate = date.toInstant()
                        .atZone(ZoneId.systemDefault()) // 시스템 기본 시간대 사용
                        .toLocalDate();
                return localDate.toString();
            } else {
                return String.valueOf(cell.getNumericCellValue());
            }
        } else if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.BLANK) {
            return "";
        } else {
            return cell.toString();
        }
      }

      @Transactional
      public Participant createParticipantAndParticipate(ParticipantCreateRequest request) {
        Gender gender = Gender.valueOf(request.getGender());
        Division division = Division.valueOf(request.getDivision());
        Integer bibNumber = Math.toIntExact(participantRepository.count() + 1);

        Participant participant = Participant.builder()
            .nameKr(request.getNameKr())
            .bibNumber(bibNumber)
            .birth(request.getBirth())
            .phone(request.getPhone())
            .emergencyContact(request.getEmergencyContact())
            .email(request.getEmail())
            .gender(gender)
            .residence(request.getResidence())
            .division(division)
            .memo(request.getMemo())
            .oneLiner(request.getOneLiner())
            .userStatus(UserStatus.WAITING)
            .build();

        for (String eventToParticipate : request.getEventToParticipate()) {
          DisciplineCode disciplineCode = DisciplineCode.valueOf(eventToParticipate);
          ContestEvent contestEvent = contestEventRepository
              .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
              .orElseThrow(() -> new ContestEventNotFound(division, disciplineCode));

          participant.participate(contestEvent);
        }

        return participantRepository.save(participant);
      }

      @Transactional
      public List<ParticipantCreateRequest> getAllUsers() {
            List<ParticipantCreateRequest> out = new ArrayList<>();
            List<Participant> participants = new ArrayList<>(participantRepository.findAll());
            for (Participant participant : participants) {
                ParticipantCreateRequest tmp = ParticipantCreateRequest.builder()
                        .nameKr(participant.getNameKr())
                        .birth(participant.getBirth())
                        .phone(participant.getPhone())
                        .emergencyContact(participant.getEmergencyContact())
                        .email(participant.getEmail())
                        .gender(participant.getGender().toString())
                        .residence(participant.getResidence())
                        .division(participant.getDivision().toString())
                        .memo(participant.getMemo())
                        .oneLiner(participant.getOneLiner())
                        .build();
                out.add(tmp);
            }

            return out;
      }
}
