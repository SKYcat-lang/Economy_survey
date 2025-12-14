from fastapi import FastAPI, HTTPException
import pandas as pd
from pandas_datareader import wb
import numpy as np
import uvicorn

app = FastAPI()

# 데이터를 서버 메모리에 저장해둘 전역 변수
cached_data = None

def fetch_economic_data():
    # ==========================================
    # 1. 설정 및 데이터 정의
    # ==========================================
    countries = ['KR', 'US', 'CN', 'JP', 'DE', 'RU', 'VN', 'IN'] 
    start_year = 2005 
    end_year = 2024

    indicators = {
        'FS.AST.PRVT.GD.ZS': 'TotalCredit_GDP', # 금융부문 총신용 (X축)
        'NE.GDI.FTOT.ZS': 'Capex_GDP',          # 설비투자 (Y축)
        'CM.MKT.LCAP.GD.ZS': 'MarketCap_GDP'    # 시가총액 (버블 크기)
    }

    try:
        # ==========================================
        # 2. WB 데이터 다운로드 및 전처리
        # ==========================================
        print(">>> World Bank 데이터 다운로드 시작 (시간이 걸릴 수 있습니다)...")
        df = wb.download(indicator=list(indicators.keys()), country=countries, start=start_year, end=end_year)
        df = df.rename(columns=indicators)
        df = df.reset_index()
        df['year'] = df['year'].astype(int)

        # 국가 매핑 및 정리
        df['country'] = df['country'].str.strip()
        code_map = {
            'Korea, Rep.': 'KR', 'Republic of Korea': 'KR',
            'United States': 'US', 'USA': 'US', 
            'China': 'CN', 'China, P.R.': 'CN',
            'Japan': 'JP', 'Germany': 'DE',
            'Russian Federation': 'RU', 'Russia': 'RU',
            'Vietnam': 'VN', 'Viet Nam': 'VN', 'India': 'IN'
        }
        df['iso'] = df['country'].map(code_map)
        df = df.dropna(subset=['iso'])
        df = df.sort_values(['iso', 'year'])

        # 데이터 보간
        numeric_cols = ['TotalCredit_GDP', 'Capex_GDP', 'MarketCap_GDP']
        df[numeric_cols] = df.groupby('iso')[numeric_cols].transform(
            lambda x: x.interpolate(limit_direction='both')
        )

        # ==========================================
        # 3. 가계부채 데이터 병합 (Color Gradient용)
        # ==========================================
        household_debt_manual = {
            'KR': {2005: 64, 2010: 73, 2015: 83, 2020: 103, 2024: 99},
            'US': {2005: 87, 2010: 91, 2015: 78, 2020: 79,  2024: 73},
            'CN': {2005: 11, 2010: 27, 2015: 39, 2020: 61,  2024: 64},
            'JP': {2005: 61, 2010: 59, 2015: 56, 2020: 65,  2024: 66},
            'DE': {2005: 68, 2010: 59, 2015: 53, 2020: 57,  2024: 53},
            'RU': {2005: 5,  2010: 10, 2015: 15, 2020: 20,  2024: 22},
            'VN': {2005: 10, 2010: 15, 2015: 28, 2020: 61,  2024: 63},
            'IN': {2005: 10, 2010: 15, 2015: 19, 2020: 35,  2024: 42}
        }

        hh_data_list = []
        for iso, data in household_debt_manual.items():
            for yr, val in data.items():
                hh_data_list.append({'iso': iso, 'year': yr, 'HH_Debt': val})
        
        hh_df = pd.DataFrame(hh_data_list)
        
        df = pd.merge(df, hh_df, on=['iso', 'year'], how='left')
        df['HH_Debt'] = df.groupby('iso')['HH_Debt'].transform(
            lambda x: x.interpolate(method='linear', limit_direction='both')
        )

        valid_isos = df[df['TotalCredit_GDP'] > 0]['iso'].unique()
        df = df[df['iso'].isin(valid_isos)]

        # NaN 값 처리 (JSON 변환 에러 방지)
        df = df.replace({np.nan: None})
        
        result = {}
        for iso in df['iso'].unique():
            country_data = df[df['iso'] == iso][['year', 'TotalCredit_GDP', 'Capex_GDP', 'MarketCap_GDP', 'HH_Debt']]
            result[iso] = country_data.to_dict(orient='records')
            
        print(">>> 데이터 준비 완료!")
        return result

    except Exception as e:
        print(f"Error: {e}")
        return None

# [중요 1] 서버 시작 시 데이터를 미리 로딩 (캐싱)
@app.on_event("startup")
def startup_event():
    global cached_data
    cached_data = fetch_economic_data()

# [중요 2] 경로를 "/"로 수정 (Java Controller가 localhost:8000/ 으로 요청하므로)
@app.get("/") 
def read_root():
    global cached_data
    # 만약 처음에 실패했다면 다시 시도
    if cached_data is None:
        cached_data = fetch_economic_data()
        
    if cached_data is None:
        raise HTTPException(status_code=500, detail="데이터 수집 실패")
    
    return cached_data

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)