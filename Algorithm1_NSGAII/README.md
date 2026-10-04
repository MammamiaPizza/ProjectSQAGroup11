# คู่มือติดตั้งและตั้งค่า NSGA-II / EvoSuite บน Defects4J

เอกสารนี้ครอบคลุม 3 เรื่องเท่านั้น: **สิ่งที่ต้องมี (Prerequisites)**, **วิธีติดตั้งทีละขั้น**,
และ **วิธีตั้งค่า (flag ต่าง ๆ)** — ไม่พูดถึงผลลัพธ์การทดลอง เพราะมีเอกสารอีกฉบับดูแลส่วนนั้น

ทุกคำสั่งในเอกสารนี้รันใน **WSL Ubuntu** ทั้งหมด ไม่ใช่ PowerShell ของ Windows

\---

## 1\. สภาพแวดล้อมที่ใช้จริงในการทดลอง

ตารางนี้ตรวจจากเครื่องจริงเมื่อวันที่ 2026-10-04 ไม่ใช่ค่าที่คาดเดา ใช้เป็นเกณฑ์เทียบได้ว่า
เครื่องของตัวเองตรงกับที่เคยรันไว้หรือไม่

|รายการ|ค่าที่ตรวจได้|ใช้ทำอะไร|
|-|-|-|
|Host OS|Windows, build 10.0.26200, x64|เครื่องหลัก|
|WSL|WSL2, kernel `6.18.33.2-microsoft-standard-WSL2`|รันทุกคำสั่ง|
|Distribution ที่ใช้|`Ubuntu` — Ubuntu 26.04.1 LTS|สภาพแวดล้อมจริงของงาน|
|ผู้ใช้ใน WSL|`pizza` → home `/home/pizza`|ทุก path ในเอกสารอ้างอันนี้|
|Java default (`java`)|OpenJDK **11.0.32.1**+1-post-1ubuntu1-26.04-Ubuntu|Defects4J ใช้ตัวนี้|
|Java 8|OpenJDK **1.8.0\_504** ที่ `/usr/lib/jvm/java-8-openjdk-amd64/`|EvoSuite ใช้ตัวนี้|
|`tools.jar`|มีอยู่ ขนาด 18,362,326 ไบต์|EvoSuite 1.2.0 จำเป็นต้องมี|
|Python|**3.14.4**|รันสคริปต์ทั้งสองตัว|
|Defects4J|**v3.0.1-7-g8c16da82** (commit `8c16da82`, 2026-04-07) ที่ `\~/defects4j`|checkout / compile / test / coverage|
|จำนวนโปรเจกต์ที่ build แล้ว|**17** (`defects4j pids` คืน 17 บรรทัด)|ตรงกับที่ใช้ทดลอง|
|`JAVA\_HOME`|**ไม่ได้ตั้ง** (unset) — และ Defects4J ทำงานได้ปกติ|ไม่ต้องตั้งก็ได้|
|เครื่องมือ build ที่มี|`mvn`, `ant`, `git`, `perl`, `cpan`, `unzip`, `tar`, `bzip2`|`init.sh` ของ Defects4J ต้องใช้|
|EvoSuite|1.2.0 (`evosuite-1.2.0.jar` 19,766,471 ไบต์)|ตัวสร้างเทสต์|

**โฟลเดอร์ทำงานเดิมที่ยังค้างอยู่ในเครื่อง** (ไม่จำเป็นต้องมีเพื่อรันใหม่):
`/home/pizza/evosuite` (467 MB, รอบ 1) และ `/home/pizza/TestRound2` (407 MB, รอบ 2)

\---

## 2\. สิ่งที่ต้องมีก่อนเริ่ม (Prerequisites)

เรียงตามลำดับที่ต้องติดตั้ง ห้ามข้าม เพราะขั้นถัดไปพึ่งขั้นก่อนหน้า

|#|สิ่งที่ต้องมี|ทำไมต้องมี|
|-|-|-|
|1|Windows 10/11 ที่เปิดใช้ WSL2 ได้|ทุกอย่างรันบน Linux|
|2|WSL2 + Ubuntu|สคริปต์อ้าง path แบบ Linux ทั้งหมด|
|3|**Java 11**|Defects4J 3.x ต้องการ Java 11|
|4|**Java 8 JDK** (ต้องเป็น JDK ไม่ใช่ JRE)|EvoSuite 1.2.0 ต้องการ `tools.jar` ซึ่งไม่มีใน JDK รุ่นหลัง 8|
|5|`git`, `perl`, `maven`, `ant`|Defects4J ใช้ตอน `init.sh`|
|6|Defects4J ที่ `init.sh` เสร็จแล้ว|ถ้าไม่ init จะ checkout โปรเจกต์ไม่ได้|
|7|Python 3|รัน `run\_all\_defects4j.py` และ `evosuite\_generator.py`|
|8|EvoSuite 1.2.0 ทั้ง 2 jar|**มีอยู่ใน repo แล้ว ไม่ต้องโหลด**|
|9|พื้นที่ว่างและเวลา|ต่อ 1 บั๊ก ใช้เวลา ≈ `budget` × จำนวนคลาสเป้าหมาย|

**ต้องติดตั้ง Java ทั้งสองรุ่นพร้อมกัน** นี่คือเงื่อนไขที่พลาดบ่อยที่สุด — EvoSuite จะพังทันทีถ้า
เอา Java 11 ไปรัน และ Defects4J ก็ไม่รองรับ Java 8 สคริปต์เลือก Java 8 เองผ่าน flag `--java-bin`
ไม่ได้พึ่ง `PATH` ดังนั้นทั้งสองตัวต้องอยู่ในเครื่องพร้อมกัน

\---

## 3\. ติดตั้งทีละขั้น

### ขั้นที่ 1 — ติดตั้ง WSL และ Ubuntu

รันใน **PowerShell (เปิดแบบ Administrator)**:

```powershell
wsl --install -d Ubuntu
```

จากนั้นรีบูตเครื่อง แล้วตั้ง username / password ของ Ubuntu

> เครื่องนี้อาจมี `docker-desktop` เป็น distro เริ่มต้น ซึ่ง\*\*ไม่มี `bash`\*\* ถ้าสั่ง
> `wsl` เฉย ๆ จะได้ error `execvpe(bash) failed` ให้ระบุ distro เสมอ: `wsl -d Ubuntu`

ตรวจว่าสำเร็จ:

```bash
wsl -d Ubuntu -e bash -ic '. /etc/os-release; echo "$PRETTY\_NAME"'
```

### ขั้นที่ 2 — ติดตั้ง Java ทั้งสองรุ่น และเครื่องมือ build

```bash
sudo apt update
sudo apt install -y openjdk-11-jdk openjdk-8-jdk \\
                    git perl maven ant unzip tar bzip2
```

ตรวจว่า Java 11 เป็นตัว default:

```bash
java -version
```

ต้องขึ้น `openjdk version "11..."`

ตรวจว่า Java 8 และ `tools.jar` มีจริง — **สองคำสั่งนี้ต้องผ่านทั้งสองอัน ไม่งั้น EvoSuite จะรันไม่ได้**:

```bash
/usr/lib/jvm/java-8-openjdk-amd64/bin/java -version
ls -la /usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar
```

คำสั่งแรกต้องขึ้น `1.8.0\_xxx` และคำสั่งสองต้องเห็นไฟล์ `tools.jar`

> ไม่ต้องสลับ `java` ไปมาระหว่างสองรุ่น และไม่ต้องตั้ง `JAVA\_HOME`
> เครื่องที่ใช้ทดลองจริงก็ไม่ได้ตั้ง Defects4J ทำงานได้ปกติ

### ขั้นที่ 3 — ติดตั้ง Defects4J

```bash
cd \~
git clone https://github.com/rjust/defects4j.git
cd \~/defects4j
./init.sh
```

`init.sh` จะ build framework และ checkout โปรเจกต์ย่อยทั้งหมด **ใช้เวลาหลายนาที** และต้องต่อ
อินเทอร์เน็ต ห้ามขัดจังหวะ

เพิ่มเข้า `PATH` — เปิดไฟล์ `\~/.bashrc` แล้วต่อท้ายด้วยบรรทัดนี้:

```bash
export PATH=$PATH:\~/defects4j/framework/bin
```

> เครื่องที่ใช้ทดลองจริงมีบรรทัดนี้ที่ `\~/.bashrc` บรรทัดที่ 121

ทำให้มีผลทันที:

```bash
source \~/.bashrc
```

ตรวจว่าสำเร็จ — สองคำสั่งนี้คือเกณฑ์ผ่านของขั้นที่ 3:

```bash
which defects4j      # ต้องได้ /home/<user>/defects4j/framework/bin/defects4j
defects4j pids | wc -l    # ต้องได้ 17
```

### ขั้นที่ 4 — เตรียม EvoSuite

**ไม่ต้องดาวน์โหลด** jar ทั้งสองไฟล์ถูก commit ไว้ใน repo แล้วที่
`Algorithm1\_NSGAII/Code/Round1/` และ `Code/Round2/`:

* `evosuite-1.2.0.jar` (19,766,471 ไบต์) — ตัวสร้างเทสต์
* `evosuite-standalone-runtime-1.2.0.jar` (7,374,608 ไบต์) — runtime ที่เทสต์ที่สร้างได้ต้อง link
ตอนถูก `defects4j test` / `defects4j coverage` เรียกไปรัน

ถ้าต้องการโหลดใหม่ (กรณีไฟล์หาย) ไปที่หน้า releases ของ EvoSuite:
`https://github.com/EvoSuite/evosuite/releases` แล้วเลือกแท็ก `v1.2.0`
**ต้องใช้เวอร์ชัน 1.2.0 เท่านั้น** เพราะรุ่นใหม่กว่าตัด `tools.jar` ทิ้งและ flag บางตัวเปลี่ยน

### ขั้นที่ 5 — เตรียมไฟล์ของโปรเจกต์

โฟลเดอร์ `Algorithm1\_NSGAII/Code/Round1/` (และ `Round2/`) ต้องมีครบ 4 ไฟล์นี้:

|ไฟล์|ขนาด|หน้าที่|
|-|-|-|
|`run\_all\_defects4j.py`|15,785 (R1) / 16,998 (R2) ไบต์|**ตัวขับหลัก** วนทุกบั๊กแล้วสั่งงานทั้งหมดอัตโนมัติ|
|`evosuite\_generator.py`|9,815 ไบต์ (R1 = R2 เหมือนกันทุกไบต์)|ตัวห่อ EvoSuite สร้างเทสต์จากบั๊กเดียว|
|`evosuite-1.2.0.jar`|19,766,471 ไบต์|EvoSuite|
|`evosuite-standalone-runtime-1.2.0.jar`|7,374,608 ไบต์|runtime ของเทสต์|

ตรวจว่ามีครบ:

```bash
ls -la Algorithm1\_NSGAII/Code/Round1/
```

### ขั้นที่ 6 — ตรวจสอบความพร้อมรวม

รัน 5 คำสั่งนี้ ถ้าผ่านหมดแปลว่าพร้อมรันจริง:

```bash
defects4j pids | wc -l                                        # 17
python3 -V                                                    # Python 3.x
/usr/lib/jvm/java-8-openjdk-amd64/bin/java -version           # 1.8.0\_xxx
ls -l /usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar         # ไฟล์มีอยู่
ls Algorithm1\_NSGAII/Code/Round1/evosuite-1.2.0.jar           # jar มีอยู่
```

\---

## 4\. สคริปต์ทำงานอัตโนมัติอย่างไร

`run\_all\_defects4j.py` คือตัวขับหลัก แค่รันมันตัวเดียว มันจะจัดการทุกอย่างต่อบั๊กตามลำดับนี้
**ไม่ต้องสั่ง defects4j เองทีละขั้น**:

1. `defects4j pids` — เรียกครั้งเดียวตอนเริ่ม เพื่อไล่รายชื่อโปรเจกต์ทั้งหมด
2. `defects4j checkout -p <pid> -v <bid>b -w <workspace>/<pid>-<bid>b` — checkout เวอร์ชัน buggy
3. `defects4j checkout -p <pid> -v <bid>f -w <workspace>/<pid>-<bid>f` — checkout เวอร์ชัน fixed
4. `defects4j export -p classes.modified -w <buggy>` — หาคลาสเป้าหมายของบั๊กนั้น
5. `defects4j compile -w <buggy>` **และ** `defects4j compile -w <fixed>` — compile ทั้งคู่เสมอ
เพราะ EvoSuite ต้องมีไฟล์ `.class` ให้ instrument และ `defects4j coverage` ก็ต้องการ `.class`
ของฝั่ง fixed
6. เรียก `evosuite\_generator.py` — ตัวนี้จะรัน
`defects4j export -p cp.compile -w <buggy>` เพื่อเอา classpath ไปส่งให้ EvoSuite
แล้วสร้าง `<out>/tests.tar.bz2`
7. `defects4j coverage -w <fixed> -s <out>/tests.tar.bz2` — วัด coverage บนเวอร์ชัน fixed
8. `defects4j test -w <buggy> -s <out>/tests.tar.bz2` — รันเทสต์บนเวอร์ชัน buggy

ก่อนขั้น 7 และ 8 สคริปต์จะลบ `summary.csv` และ `failing\_tests` ที่ค้างอยู่ก่อนเสมอ เพื่อไม่ให้ผล
ของบั๊กก่อนหน้าหลุดมาปน และเมื่อจบแต่ละบั๊ก checkout ทั้งสองจะถูกลบทิ้ง (ยกเว้นใส่ `--keep`)

**ข้อสำคัญ:** `evosuite\_generator.py` อ่าน**เฉพาะ**โฟลเดอร์ buggy เท่านั้น ไม่แตะ fixed เลย
flag `--fixed` รับค่ามาแล้วไม่ได้ใช้ เก็บไว้เพื่อให้ placeholder ของตัวขับใช้ชุดเดียวกันได้
นี่เป็นเรื่องตั้งใจ ตาม protocol ของกลุ่มที่ห้ามตัวสร้างเทสต์เห็นซอร์สฝั่ง fixed

\---

## 5\. โครงสร้างโฟลเดอร์

```
<out-root>/project-<pid>/<pid>-<bid>/round<run-id>/
    compile\_b.log                  ผล defects4j compile ฝั่ง buggy
    compile\_f.log                  ผล defects4j compile ฝั่ง fixed
    generator.log                  stdout/stderr ของ evosuite\_generator.py เท่านั้น
    evosuite-work/                 โฟลเดอร์ -base\_dir ของ EvoSuite
    evosuite-logs/<คลาส>.log       \*\*\* บรรทัดแรกคือ "CMD: <คำสั่ง java จริง>" \*\*\*
    tests.tar.bz2                  เทสต์ที่สร้างได้ (รูปแบบที่ defects4j test -s รับได้)
    fixed\_coverage.log             ผล defects4j coverage -w fixed
    fixed\_coverage\_summary.csv     สำเนา summary.csv ของฝั่ง fixed
    buggy\_test.log                 ผล defects4j test -w buggy
    buggy\_failing\_tests.txt        สำเนา failing\_tests ของฝั่ง buggy

<tests-root>/round<run-id>/project-<pid>/<pid>-<bid>/
    <package path>/<Class>\_ESTest.java     ซอร์ส JUnit4 ที่สร้างได้

<workspace>/<pid>-<bid>b , <pid>-<bid>f    checkout ชั่วคราว ถูกลบถ้าไม่ใส่ --keep
```

**log กับเทสต์ที่สร้างได้ถูกแยกไว้คนละโฟลเดอร์โดยตั้งใจ** เพื่อไม่ให้ซอร์สเทสต์ปนไปกับหลักฐาน
`evosuite\_generator.py` จะห่อ `tests.tar.bz2` ก่อน แล้วค่อย**ย้าย**ไฟล์ `.java` ไปที่ `--tests-out`

> ระวัง: คำสั่ง EvoSuite จริง ๆ \*\*ไม่ได้อยู่ใน `generator.log`\*\*
> `generator.log` มีแค่ stdout ของสคริปต์ เช่นบรรทัด `ok <ชื่อคลาส>`
> คำสั่งเต็มอยู่ที่\*\*บรรทัดแรก\*\*ของ `evosuite-logs/<ชื่อคลาสเต็ม>.log` ขึ้นต้นด้วย `CMD: `

\---

## 6\. ทดสอบก่อนรันจริง

**ห้ามรันเต็ม 854 บั๊กทันที** ให้ทดสอบ 3 ขั้นนี้ก่อน

### 6.1 ขั้นตรวจความพร้อม (ไม่สร้างเทสต์)

ถ้าไม่ใส่ `--generator` สคริปต์จะทำแค่ checkout + compile ใช้ตรวจว่า Defects4J พร้อมหรือยัง:

```bash
python3 Algorithm1\_NSGAII/Code/Round1/run\_all\_defects4j.py \\
  --projects Lang --bugs 1-3 \\
  --workspace \~/sqa-ws --out-root \~/sqa-out --results \~/sqa-out/check.csv
```

ผ่าน = ใน CSV ได้ `status` เป็น `checkout\_compile\_ok`

### 6.2 ขั้นนับงาน (ไม่แตะอะไรเลย)

```bash
python3 Algorithm1\_NSGAII/Code/Round1/run\_all\_defects4j.py --dry-run
```

ใช้ดูว่าจะมีงานกี่ชิ้น โดยไม่ checkout ไม่รันอะไรเลย

### 6.3 ขั้นรันจริงแบบย่อ

```bash
python3 Algorithm1\_NSGAII/Code/Round1/run\_all\_defects4j.py \\
  --projects Lang --bugs 1-3 \\
  --method nsga2 --run-id 99 \\
  --detect-direction pass\_buggy\_fail\_fixed \\
  --workspace \~/sqa-ws --out-root \~/sqa-out \\
  --tests-root \~/sqa-tests --results \~/sqa-out/smoke.csv \\
  --generator "python3 Algorithm1\_NSGAII/Code/Round1/evosuite\_generator.py \\
      --buggy {buggy} --fixed {fixed} --out {out} --classes {classes} \\
      --seed {seed} --tests-out {tests\_dir} \\
      --evosuite-jar Algorithm1\_NSGAII/Code/Round1/evosuite-1.2.0.jar \\
      --budget 30"
```

ลด `--budget` เหลือ 30 เพื่อให้จบเร็ว **ผ่านหรือไม่ให้ดูจากโครงสร้างไฟล์ ไม่ใช่จากผลเทสต์**:

```bash
ls \~/sqa-out/project-Lang/Lang-1/round99/          # ต้องเห็น tests.tar.bz2
head -1 \~/sqa-out/project-Lang/Lang-1/round99/evosuite-logs/\*.log   # ต้องขึ้นต้นด้วย CMD:
ls \~/sqa-tests/round99/project-Lang/Lang-1/        # ต้องเห็นไฟล์ \*\_ESTest.java
```

ถ้าครบ 3 อย่างนี้แปลว่าระบบต่อถูกหมดแล้ว

\---

## 7\. คำสั่งรันจริง

ค่า path ทั้งหมดนี้ดึงมาจาก log ของการรันจริง

### 7.1 รอบ 1 — `run\_id=02`, budget 150 วินาทีต่อคลาส

```bash
python3 Algorithm1\_NSGAII/Code/Round1/run\_all\_defects4j.py \\
  --method nsga2 \\
  --run-id 02 \\
  --detect-direction pass\_buggy\_fail\_fixed \\
  --workspace  /home/pizza/evosuite/Test \\
  --out-root   /home/pizza/evosuite/results \\
  --tests-root /home/pizza/evosuite/TestGenerate/test \\
  --results    <path ของ summary\_nsga2.csv>        # ไม่มีบันทึกไว้ใน log ใด ๆ
  --generator "python3 Algorithm1\_NSGAII/Code/Round1/evosuite\_generator.py \\
      --buggy {buggy} --fixed {fixed} --out {out} --classes {classes} \\
      --seed {seed} --tests-out {tests\_dir} \\
      --evosuite-jar /home/pizza/evosuite/evosuite-1.2.0.jar \\
      --budget 150"
```

### 7.2 รอบ 2 — `run\_id=03`, budget 60 วินาทีต่อคลาส

```bash
python3 Algorithm1\_NSGAII/Code/Round2/run\_all\_defects4j.py \\
  --method nsga2 \\
  --run-id 03 \\
  --detect-direction pass\_buggy\_fail\_fixed \\
  --workspace  /home/pizza/TestRound2/Test \\
  --out-root   /home/pizza/TestRound2/results \\
  --tests-root /home/pizza/TestRound2/TestGenerate/test \\
  --results    <path ของ summary\_nsga2.csv>        # ไม่มีบันทึกไว้
  --generator "python3 Algorithm1\_NSGAII/Code/Round2/evosuite\_generator.py \\
      --buggy {buggy} --fixed {fixed} --out {out} --classes {classes} \\
      --seed {seed} --tests-out {tests\_dir} \\
      --evosuite-jar /home/pizza/TestRound2/evosuite-1.2.0.jar \\
      --budget 60"
```

การทดลองจริงรันบน **2 เครื่อง** ในทั้งสองรอบ เครื่องที่สองเหมือนกันทุกค่าแต่เปลี่ยน
`/home/pizza/...` เป็น `/home/user/...`

**`--budget` คือความต่างเดียวที่มีนัยสำคัญระหว่างสองรอบ** รอบ 1 ใช้ 150 รอบ 2 ใช้ 60
และค่า default ในโค้ดคือ 60 ซึ่งตรงกับรอบ 2 เท่านั้น

### 7.3 รันต่อหลังจากหยุดกลางคัน

CSV ผลลัพธ์เขียนแบบ **append เท่านั้น ไม่เคยเขียนทับ** ดังนั้นถ้ารันคำสั่งเดิมซ้ำ บั๊กที่เคยล้มเหลว
จะถูกทำใหม่เองอัตโนมัติและเพิ่มเป็นแถวใหม่ ส่วนบั๊กที่เคยสำเร็จจะไม่ถูกทำซ้ำ
ถ้าต้องการบังคับทำใหม่ทั้งหมดให้ใส่ `--force`

\---

## 8\. ตาราง flag ทั้งหมด

### 8.1 `run\_all\_defects4j.py`

|Flag|ถ้าไม่ระบุ (default)|ความหมาย|ค่าที่ใช้จริง|
|-|-|-|-|
|`--projects`|**ทุกโปรเจกต์**|กรองโปรเจกต์ คั่นด้วย `,` เช่น `Lang,Math` — ชื่อที่ไม่รู้จักจะทำให้จบด้วย error ทันที|ทั้ง 17 โปรเจกต์|
|`--bugs`|**ทุกบั๊ก**|เลือกบั๊กแบบช่วงและเดี่ยว เช่น `1-5,9` ใช้กับทุกโปรเจกต์ที่เลือก|ทั้งหมด|
|`--limit`|ไม่จำกัด|ทำแค่ N บั๊กแรก ไว้ทดสอบ|ไม่ได้ใช้|
|`--generator`|**ไม่มี** → ทำแค่ checkout + compile|คำสั่งสร้างเทสต์ ถ้าไม่ใส่จะได้โหมดตรวจความพร้อมอย่างเดียว|คำสั่งในหัวข้อ 7|
|`--method`|`baseline`|ชื่อวิธีที่จะบันทึกลงคอลัมน์ `method` ของ CSV|`nsga2`|
|`--detect-direction`|`fail\_buggy\_pass\_fixed`|เกณฑ์ตัดสินว่าตรวจพบบั๊ก — **ต้องใส่ `pass\_buggy\_fail\_fixed` เสมอเมื่อใช้ EvoSuite** ดูคำอธิบายด้านล่างตาราง|`pass\_buggy\_fail\_fixed`|
|`--run-id`|`01`|กลายเป็นชื่อโฟลเดอร์ `round<run-id>` และคอลัมน์ `run\_id`|`02` (รอบ 1), `03` (รอบ 2)|
|`--seed`|`11`|ส่งให้ generator ผ่าน placeholder `{seed}`|11|
|`--workspace`|`\~/sqa-workspaces`|โฟลเดอร์ checkout ชั่วคราว **ต้องอยู่นอก git repo**|`<host>/Test`|
|`--out-root`|`results`|โฟลเดอร์รากของหลักฐานต่อบั๊ก|`<host>/results`|
|`--tests-root`|`TestGenerate/test`|โฟลเดอร์รากของเทสต์ที่สร้างได้ ส่งให้ generator ผ่าน `{tests\_dir}`|`<host>/TestGenerate/test`|
|`--results`|`results/summary\_auto.csv`|ไฟล์ CSV ผลลัพธ์ **เขียนแบบ append เท่านั้น**|ไม่มีบันทึกไว้|
|`--timeout`|`1800`|วินาทีต่อ 1 คำสั่ง `defects4j`|ไม่มีบันทึกไว้|
|`--gen-timeout`|`3600`|วินาทีต่อการ generate ทั้ง 1 บั๊ก|ไม่มีบันทึกไว้|
|`--keep`|ปิด|ไม่ลบ checkout หลังจบแต่ละบั๊ก (กินพื้นที่มาก)|ไม่มีบันทึกไว้|
|`--force`|ปิด|ทำซ้ำแม้เคยบันทึกผลไว้แล้ว|ไม่มีบันทึกไว้|
|`--dry-run`|ปิด|แค่นับจำนวนงานแล้วจบ ไม่ checkout ไม่รันอะไร|ใช้ตอนวางแผน|

**Placeholder ที่แทนค่าลงใน `--generator`:**
`{pid}` `{bid}` `{buggy}` `{fixed}` `{out}` `{classes}` `{seed}` `{tests\_dir}`

**เรื่อง `--detect-direction` — ต้องอ่าน**

* `fail\_buggy\_pass\_fixed` (**ค่า default**) — เหมาะกับตัวสร้างที่เดาค่าที่ถูกต้องเอง เช่น LLM
ที่เขียน expected value เอง: ตรวจพบบั๊ก = เทสต์ fail บน buggy แล้ว pass บน fixed
* `pass\_buggy\_fail\_fixed` (**ต้องใช้อันนี้กับ EvoSuite**) — EvoSuite สร้างเทสต์จากเวอร์ชัน buggy
แล้วจด assertion จากพฤติกรรมที่เห็นจริง ดังนั้น suite จะ pass บน buggy เสมอโดยอัตโนมัติ
ตรวจพบบั๊ก = เอาไปรันบน fixed แล้ว fail

> \*\*ระวัง docstring ในโค้ด\*\* บรรทัดที่ 10 ของ `run\_all\_defects4j.py` เขียนว่า
> "ตรวจพบบั๊ก = fail บน buggy และ pass บน fixed" ซึ่งเป็นการอธิบาย\*\*ค่า default\*\*
> ไม่ใช่สิ่งที่การทดลองนี้ใช้จริง การทดลองจริงส่ง `--detect-direction pass\_buggy\_fail\_fixed`

**รายละเอียดการแทนค่า 2 อย่างที่ควรรู้:**
คำสั่ง `--generator` ถูกแยกด้วย `shlex.split` แล้วทุก token ผ่าน `os.path.expanduser`
เพราะ `shlex` ไม่ขยาย `\~` ให้ ถ้าไม่มีขั้นนี้ subprocess จะหาไฟล์ไม่เจอและทุกบั๊กจะกลายเป็น
`generation\_failed` ทั้งที่ generator ทำงานได้ปกติ และ `{classes}` คือรายการจาก
`classes.modified` มาต่อกันด้วย `,`

### 8.2 `evosuite\_generator.py`

|Flag|ถ้าไม่ระบุ (default)|ความหมาย|ค่าที่ใช้จริง|
|-|-|-|-|
|`--buggy`|**บังคับใส่**|path ของ checkout ฝั่ง buggy — เป็นโฟลเดอร์เดียวที่สคริปต์นี้เปิดอ่าน|`{buggy}`|
|`--fixed`|ไม่มี|**รับค่าไว้แต่ไม่ใช้เลย** เก็บไว้ให้ placeholder ของตัวขับใช้ชุดเดียวกัน|`{fixed}`|
|`--out`|**บังคับใส่**|โฟลเดอร์ที่จะเขียน `tests.tar.bz2` และได้ `evosuite-work/` กับ `evosuite-logs/` ด้วย|`{out}`|
|`--tests-out`|ไม่มี → ปล่อย `.java` ค้างใน `evosuite-work`|โฟลเดอร์ปลายทางที่จะ**ย้าย**ไฟล์ `.java` ไปไว้หลังห่อ archive เสร็จ|`{tests\_dir}`|
|`--classes`|**บังคับใส่**|คลาสเป้าหมาย คั่นด้วย `,`|`{classes}`|
|`--seed`|`11`|→ `-seed` เป็น flag ระดับบนของ EvoSuite **ไม่ใช่ property แบบ `-D`**|11|
|`--algorithm`|`NSGAII`|→ `-Dalgorithm`|NSGAII|
|`--budget`|`60`|วินาที**ต่อคลาส** → `-Dsearch\_budget`|**150 (รอบ 1), 60 (รอบ 2)**|
|`--criterion`|`None` → ใช้ default ของ EvoSuite เอง|เป้าหมายการค้นหา คั่นด้วย `:` → `-criterion`|ไม่ได้ส่ง flag นี้|
|`--evosuite-jar`|**บังคับใส่**|path ของ jar — ตรวจว่ามีอยู่จริงก่อนเริ่มงาน ถ้าไม่เจอจะจบด้วย exit 1 ทันที|ดูหัวข้อ 7|
|`--java-bin`|`/usr/lib/jvm/java-8-openjdk-amd64/bin/java`|EvoSuite ต้องใช้ Java 8 ไม่ใช่ Java 11 ที่ Defects4J ใช้|ใช้ค่า default|
|`--tools-jar`|`/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar`|ต้องเป็น `tools.jar` ของ JDK ตัวเดียวกับ `--java-bin` — ใส่ `''` เพื่อปิด|ใช้ค่า default|
|`--per-class-timeout`|`180`|จำกัดเวลารวมต่อ 1 คลาส **ต้องมากกว่า `--budget` เสมอ**|ไม่มีบันทึกไว้|

**ถ้าไม่ส่ง `--criterion`** EvoSuite จะใช้ชุด default ของมันเอง ซึ่งยืนยันจาก `statistics.csv` แล้วว่าเป็น:

```
LINE:BRANCH:EXCEPTION:WEAKMUTATION:OUTPUT:METHOD:METHODNOEXCEPTION:CBRANCH
```

**พฤติกรรมของสคริปต์:** คลาสถูกทำทีละตัว และจะนับว่าสำเร็จก็ต่อเมื่อ EvoSuite จบด้วย exit code 0
**และ** มีไฟล์ `<work>/evosuite-tests/<package>/<Class>\_ESTest.java` เกิดขึ้นจริง
ถ้าไม่มีคลาสไหนสำเร็จเลย จะไม่มีการห่อ archive และสคริปต์จบด้วย exit 1
ถ้าเกินเวลา จะบันทึกลง log ว่า `TIMEOUT after <n>s` และนับเป็นล้มเหลว

\---

## 9\. ค่า configuration ที่ใช้จริง

ยืนยันจากการอ่านบรรทัด `CMD:` ใน `evosuite-logs/` **ทุกบรรทัดของทั้งสองรอบ** ไม่ใช่การสุ่มตัวอย่าง
และค่าตรงกันหมดทุกบรรทัด ไม่มีความแปรผัน

คำสั่ง EvoSuite ที่ถูกรันจริง:

```
/usr/lib/jvm/java-8-openjdk-amd64/bin/java \\
  -Dtools\_jar\_location=/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar \\
  -jar <install>/evosuite-1.2.0.jar \\
  -generateSuite \\
  -class <ชื่อคลาสเต็ม> \\
  -projectCP <ผลจาก defects4j export -p cp.compile -w <buggy>> \\
  -base\_dir <out-root>/project-<pid>/<pid>-<bid>/round<run-id>/evosuite-work \\
  -seed 11 \\
  -Dalgorithm=NSGAII \\
  -Dsearch\_budget=<150 ในรอบ 1, 60 ในรอบ 2> \\
  -Dtest\_format=JUNIT4 \\
  -Dassertions=true
```

|ค่า|รอบ 1|รอบ 2|
|-|-|-|
|`-Dsearch\_budget`|**150** ทุกบรรทัด|**60** ทุกบรรทัด|
|`-seed`|11|11|
|`-Dalgorithm`|NSGAII|NSGAII|
|`-Dtest\_format`|JUNIT4|JUNIT4|
|`-Dassertions`|true|true|
|`-criterion`|ไม่ปรากฏเลย|ไม่ปรากฏเลย|
|`java`|`/usr/lib/jvm/java-8-openjdk-amd64/bin/java`|เหมือนกัน|

**`-Dtools\_jar\_location` ต้องอยู่ก่อน `-jar` เสมอ** เพราะเป็น system property ของ JVM
ถ้าเอาไปวางหลัง `-jar` มันจะถูกส่งให้ EvoSuite แทนที่จะถึง JVM และ EvoSuite 1.2.0 บน Java 8
หา `tools.jar` เองไม่เจอ ต้องชี้ path ให้เอง

**ความต่างของโค้ดระหว่างสองรอบ:** `evosuite\_generator.py` เหมือนกันทุกไบต์
ส่วน `run\_all\_defects4j.py` ต่างกันแค่ 3 จุด คือ (1) รอบ 2 import `datetime, timezone` และเพิ่ม
คอลัมน์ `timestamp` ไว้ข้างหน้า ทำให้ CSV จาก 17 คอลัมน์เป็น 18 (2) `load\_done()` ของรอบ 2
นับเฉพาะแถวที่ `status == "ok"` ว่าเสร็จแล้ว บั๊กที่ล้มเหลวจึงถูกลองใหม่เองเมื่อรันคำสั่งเดิมซ้ำ
และ (3) รอบ 2 บันทึก timestamp ลงทุกแถว

\---

## 10\. สิ่งที่ยังไม่ทราบ

รายการนี้ตรวจแล้วไม่พบในไฟล์ใด ๆ ของ repo จึงระบุไว้ตรง ๆ ดีกว่าให้เดา

* path ของ `--results` ที่ใช้ในแต่ละรอบ
* `--timeout`, `--gen-timeout`, `--keep`, `--force`, `--per-class-timeout` ถูกเปลี่ยนจาก default หรือไม่
* เวอร์ชันที่แน่นอนของ Defects4J และ Python **บนเครื่องที่รันรอบ 1 และรอบ 2**
(ตารางในหัวข้อ 1 คือค่าของเครื่อง ณ วันที่ 2026-10-04)
* สเปกฮาร์ดแวร์ของเครื่องทั้งสองที่ใช้รัน

\---

## 11\. ปัญหาที่พบบ่อย

### 11.1 `wsl` เฉย ๆ แล้วได้ `execvpe(bash) failed`

distro เริ่มต้นของเครื่องเป็น `docker-desktop` ซึ่งไม่มี `bash` ให้ระบุ distro เสมอ:

```bash
wsl -d Ubuntu
```

ถ้าต้องการตั้ง Ubuntu เป็น default:

```powershell
wsl --set-default Ubuntu
```

### 11.2 สั่ง `defects4j` แล้วได้ `command not found`

บรรทัด `export PATH` อยู่ใน `\~/.bashrc` ซึ่ง **login shell ไม่อ่าน** — นี่คือเหตุผลว่าทำไมสั่ง
`wsl -e bash -lc '...'` แล้วหา `defects4j` ไม่เจอ ทั้งที่ติดตั้งถูกแล้ว
ให้ใช้ interactive shell หรือ source เอง:

```bash
wsl -d Ubuntu -e bash -ic 'source \~/.bashrc; defects4j pids'
```

### 11.3 หา configuration จริงใน `generator.log` แล้วไม่เจอ

มันไม่ได้อยู่ที่นั่น `generator.log` มีแค่ stdout ของสคริปต์
คำสั่ง java เต็ม ๆ อยู่ที่บรรทัดแรกของ `evosuite-logs/<ชื่อคลาสเต็ม>.log`

### 11.4 เห็น `--budget` default เป็น 60 แล้วสรุปว่ารอบ 1 ใช้ 60

รอบ 1 ใช้ 150 ค่า default ตรงกับรอบ 2 เท่านั้น ต้องอ่านจาก log เสมอ

### 11.5 EvoSuite รันแล้วพังทันทีเรื่อง `tools.jar`

เอา Java 11 ไปรัน EvoSuite ให้ใช้ `--java-bin` ชี้ไป Java 8 และตรวจว่า
`/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar` มีอยู่จริง

### 11.6 `--per-class-timeout` น้อยกว่า `--budget`

EvoSuite จะถูก kill กลางคัน แล้วถูกบันทึกเป็น `generation\_failed` ซึ่งดูเหมือน EvoSuite พัง
แต่จริง ๆ เป็นแค่ตั้ง timeout ผิด รอบ 1 ใช้ budget 150 ซึ่งยังน้อยกว่า default 180
**ถ้าจะเพิ่ม `--budget` เกิน 180 ต้องเพิ่ม `--per-class-timeout` ไปด้วย**

### 11.7 วาง `--workspace` ไว้ใน git repo

checkout มีขนาดใหญ่และถูกลบทิ้งทุกบั๊ก ให้วางไว้นอก repo เสมอ

### 11.8 CSV มีหลายแถวต่อบั๊ก

เป็นเรื่องปกติ เพราะไฟล์เขียนแบบ append เท่านั้น และรอบ 2 จะลองใหม่เองทุกครั้งที่รันคำสั่งเดิมซ้ำ
ดูหัวข้อ 7.3

### 11.9 ชื่อโฟลเดอร์ `round` ไม่ตรงกับ `run-id`

`Result\_Round1/LogResult.zip` เก็บโฟลเดอร์ไว้เป็น `round01/` ทั้งที่ log ข้างในอ้างถึง `round02`
เพราะโฟลเดอร์ถูกเปลี่ยนชื่อตอนบีบอัด ส่วน `Result\_Round2/LogResult.zip` ใช้ `round03/`
และไม่ได้ถูกเปลี่ยนชื่อ **ให้ยึดคอลัมน์ `run\_id` ใน CSV เสมอ อย่ายึดชื่อโฟลเดอร์**

### 11.10 `unzip` กับ wildcard บนเครื่องนี้

UnZip 6.00 ในสภาพแวดล้อมนี้จับคู่ `project-Mockito/Mockito-1/round03/evosuite-logs/\*` ได้
แต่คืนค่าว่างสำหรับ `\*/evosuite-logs/\*.log` และ `project-\*`
ถ้าต้องการกวาด log หลายไฟล์ ให้ stream ทั้ง archive แทน:

```bash
unzip -p <zip> | grep -a "^CMD: "
```

### 11.11 CSV เป็น CRLF

ไฟล์ CSV ต้นทางใช้ CRLF บน MSYS/Git-Bash ตัว `awk`, `sed` และ `grep` อ่านแบบ text mode
และจะตัด `\\r` ทิ้งเงียบ ๆ ทำให้ฟิลด์สุดท้ายเพี้ยน ตรวจด้วย `od -c` และ normalize ด้วย
`tr -d '\\r'` ก่อนประมวลผล

### 11.12 เจอไฟล์ `.tmp\_file\_needed\_by\_mock\_of\_FileHandler` ในโฟลเดอร์หลักฐาน

เป็นไฟล์ที่ Mockito สร้างตอนรันเทสต์ **ไม่ใช่ไฟล์ขยะ อย่าลบ**

